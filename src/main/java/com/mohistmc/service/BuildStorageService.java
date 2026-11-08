package com.mohistmc.service;

import com.mohistmc.config.properties.S3Properties;
import com.mohistmc.entity.Build;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

/**
 * Keeps the build files in the S3 bucket. A build is downloaded from the bucket's public URL.
 */
@Slf4j
@Service
public class BuildStorageService {
    private final S3Properties s3Properties;
    private final S3Client s3;

    public BuildStorageService(S3Properties s3Properties) {
        this.s3Properties = s3Properties;
        this.s3 = createClient(s3Properties);
    }

    private static S3Client createClient(S3Properties properties) {
        if (isBlank(properties.getBucket()) || isBlank(properties.getPublicUrl())) {
            throw new IllegalStateException("s3.bucket and s3.public-url are required: the builds are stored in a bucket");
        }

        return S3Client.builder()
                .endpointOverride(URI.create(properties.getEndpoint()))
                .region(Region.of(properties.getRegion()))
                .forcePathStyle(properties.isPathStyle())
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.getAccessKeyId(), properties.getSecretAccessKey())))
                // S3-compatible stores do not all accept the SDK's default CRC checksums; Content-MD5 covers uploads
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
                .build();
    }

    /**
     * Whether a build's file is where its row says it is.
     */
    public enum FileState {
        PRESENT,
        MISSING,
        // the storage did not answer: nothing can be concluded
        UNKNOWN
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Uploads a freshly downloaded build file and records its key on the build.
     */
    public void store(Build build, Path file) throws IOException {
        upload(build, file);
    }

    /**
     * Looks for a build's object in the bucket.
     */
    public FileState checkFile(Build build) {
        if (build.getFileKey() == null) {
            return FileState.MISSING;
        }
        try {
            s3.headObject(request -> request.bucket(s3Properties.getBucket()).key(build.getFileKey()));
            return FileState.PRESENT;
        } catch (S3Exception e) {
            return e.statusCode() == 404 ? FileState.MISSING : FileState.UNKNOWN;
        } catch (SdkException e) {
            return FileState.UNKNOWN;
        }
    }

    /**
     * Where the build is downloaded from: empty when it is not in the bucket.
     */
    public Optional<URI> getPublicUrl(Build build) {
        if (build.getFileKey() == null) {
            return Optional.empty();
        }

        return Optional.of(UriComponentsBuilder.fromUriString(s3Properties.getPublicUrl())
                .path("/" + build.getFileKey())
                .build()
                .encode()
                .toUri());
    }

    private void upload(Build build, Path file) throws IOException {
        String key = keyOf(build);
        long size = Files.size(file);

        try {
            s3.putObject(PutObjectRequest.builder()
                            .bucket(s3Properties.getBucket())
                            .key(key)
                            // downloads are redirected to the public URL: the object must be readable by anyone
                            .acl(ObjectCannedACL.PUBLIC_READ)
                            .contentType("application/java-archive")
                            .contentDisposition("attachment; filename=\"" + build.getFileName() + "\"")
                            // the bucket refuses the object if it did not receive exactly this file
                            .contentMD5(md5Base64(file))
                            .build(),
                    RequestBody.fromFile(file));

            HeadObjectResponse stored = s3.headObject(request -> request.bucket(s3Properties.getBucket()).key(key));
            if (stored.contentLength() != size) {
                throw new IOException("The bucket holds " + stored.contentLength() + " bytes for " + key + ", expected " + size);
            }
        } catch (SdkException e) {
            throw new IOException("Upload of " + key + " failed: " + e.getMessage(), e);
        }

        build.setFileKey(key);
        log.info("Build {} stored in the bucket as {}", build.getId(), key);
    }

    private String keyOf(Build build) {
        return String.join("/",
                s3Properties.getPrefix(),
                build.getProjectVersion().getProject().getName(),
                build.getProjectVersion().getVersionName(),
                build.getId().toString(),
                build.getFileName());
    }

    private static String md5Base64(Path file) throws IOException {
        try (InputStream is = Files.newInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            return Base64.getEncoder().encodeToString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }
}
