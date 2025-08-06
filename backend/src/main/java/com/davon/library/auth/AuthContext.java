package com.davon.library.auth;

import com.davon.library.model.User;
import com.davon.library.service.AuthenticationService;
import lombok.Data;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

@Data
public class AuthContext {
    private User user;
    private String token;
    private LocalDate expiry;
    private String authError;

    private final AuthenticationService authService;

    public AuthContext(AuthenticationService authService) {
        this.authService = authService;
    }

    public CompletableFuture<Boolean> login(String email, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                AuthenticationService.LoginResult result = authService.login(email, password);
                if (result.isSuccess()) {
                    this.token = result.getSessionId();
                    this.authError = null;
                    return true;
                } else {
                    this.authError = result.getMessage();
                    return false;
                }
            } catch (Exception e) {
                this.authError = "Authentication error: " + e.getMessage();
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> register(User newUser, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return authService.registerAccount(newUser, password);
            } catch (Exception e) {
                this.authError = "Registration error: " + e.getMessage();
                return false;
            }
        });
    }

    public void logout() {
        if (this.token != null) {
            authService.logout(this.token);
            this.token = null;
            this.user = null;
        }
    }

    public boolean isAuthenticated() {
        return token != null && user != null && (expiry == null || expiry.isAfter(LocalDate.now()));
    }

    public String getAuthError() {
        return authError;
    }
}
