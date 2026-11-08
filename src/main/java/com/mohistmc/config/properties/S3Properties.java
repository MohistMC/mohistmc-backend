package com.mohistmc.config.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "s3")
public class S3Properties {
    @Value("${s3.endpoint}")
    private String endpoint;

    @Value("${s3.region}")
    private String region;

    @Value("${s3.bucket}")
    private String bucket;

    // <endpoint>/<bucket>/<key> instead of <bucket>.<endpoint>/<key>, for stores without bucket subdomains
    @Value("${s3.path-style}")
    private boolean pathStyle;

    @Value("${s3.access-key-id}")
    private String accessKeyId;

    @Value("${s3.secret-access-key}")
    private String secretAccessKey;

    @Value("${s3.prefix}")
    private String prefix;

    @Value("${s3.public-url}")
    private String publicUrl;
}
