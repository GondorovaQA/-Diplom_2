package org.example;
public class UserGenerator {

    public static RegisterUser getDefaultRegistrationData() {
        RegisterUser user = new RegisterUser();
        user.setEmail("test-user@example.com");
        user.setPassword("password123");
        return user;
    }
    public static class RegisterUser {
        private String email;
        private String password;
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
        public String getAccessToken() {
            return "fake_access_token";
        }

    }
}
