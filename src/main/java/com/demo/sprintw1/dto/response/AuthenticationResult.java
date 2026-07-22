package com.demo.sprintw1.dto.response;

/*
 * Authentication işlemi sonucunda oluşan Access Token ve Refresh Token'ı taşır.
 * Bu sınıf sadece Service ile Controller arasında kullanılır.
 */
public class AuthenticationResult {

    private String accessToken;

    private String refreshToken;

    public AuthenticationResult() {
    }

    public AuthenticationResult(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}