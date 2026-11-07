package com.mohistmc.service;

import com.mohistmc.config.properties.FolderProperties;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

/**
 * Keeps the build files: in the S3 bucket when one is configured, in builds.path otherwise.
 * A build whose file_key is set is served from the bucket, any other from builds.path.
 */
@Slf4j
@Service
public class BuildStorageService {
    private final FolderProperties folderProperties;
    private final S3Properties s3Properties;

    // null when no bucket is configured
    private final S3Client s3;

    public BuildStorageService(FolderProperties folderProperties, S3Properties s3Properties) {
        this.folderProperties = folderProperties;
        this.s3Properties = s3Properties;
        this.s3 = s3Properties.isEnabled() ? createClient(s3Properties) : null;
    }

    private static S3Client createClient(S3Properties properties) {
        if (properties.getPublicUrl() == null || properties.getPublicUrl().isBlank()) {
            throw new IllegalStateException("s3.public-url is required when s3.bucket is set");
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

    public boolean usesS3() {
        return s3 != null;
    }

    public Path getLocalPath(Build build) {
        return folderProperties.getBuildPath(build);
    }

    /**
     * Stores a freshly downloaded build file, which this call consumes.
     */
    public void store(Build build, Path file) throws IOException {
        if (usesS3()) {
            upload(build, file);
        } else {
            Path destination = getLocalPath(build);
            Files.createDirectories(destination.getParent());
            Files.move(file, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Copies a build's file from builds.path to the bucket. The local file is kept.
     */
    public void migrateToS3(Build build) throws IOException {
        upload(build, getLocalPath(build));
    }

    /**
     * Where the build is downloaded from, when it is in the bucket.
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
