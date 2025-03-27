package org.example.intvincentchan00.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for JWT authentication.
 */
@Configuration
public class JwtConfig {

    @Value("${app.jwt.secret:quizbuilder_secret_key_should_be_changed_in_production}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}") // Default: 24 hours
    private int jwtExpirationMs;

    @Value("${app.jwt.header:Authorization}")
    private String jwtHeader;

    @Value("${app.jwt.prefix:Bearer }")
    private String jwtPrefix;

    // Getters
    public String getJwtSecret() {
        return jwtSecret;
    }

    public int getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    public String getJwtHeader() {
        return jwtHeader;
    }

    public String getJwtPrefix() {
        return jwtPrefix;
    }
}