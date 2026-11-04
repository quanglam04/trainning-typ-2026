package com.courseshop.courseshop.dto;

public class TokenResponse {
    private final String accessToken;
    private final String tokenType;
    private final long expiresInSeconds;
    private final String refreshToken;
    private final long refreshTokenExpiresInSeconds;

    public TokenResponse(String accessToken, String tokenType, long expiresInSeconds,
            String refreshToken, long refreshTokenExpiresInSeconds) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresInSeconds = expiresInSeconds;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresInSeconds = refreshTokenExpiresInSeconds;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public String getRefreshToken() { return refreshToken; }

    public long getRefreshTokenExpiresInSeconds() { return refreshTokenExpiresInSeconds; }
}
