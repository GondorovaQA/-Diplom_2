package org.example;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
    }

    // Геттеры и сеттеры
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
