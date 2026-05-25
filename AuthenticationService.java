package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationService {

    private final Repository repo;

    public AuthenticationService(Repository repo) {
        this.repo = repo;
    }

    // ─────────────────────────────────────────
    // EMAIL VALIDATION
    // ─────────────────────────────────────────

    public boolean isValidGmail(String email) {
        return email != null
                && email.toLowerCase().endsWith("@gmail.com")
                && email.length() > 10;
    }

    public String getEmailWarning(String email) {
        if (email == null || email.trim().isEmpty())
            return "Email cannot be empty. Please enter a Gmail address (e.g. yourname@gmail.com).";
        if (!email.contains("@"))
            return "Missing '@gmail.com'. Please enter a proper Gmail (e.g. yourname@gmail.com).";
        if (!email.toLowerCase().endsWith("@gmail.com"))
            return "Only @gmail.com addresses are accepted (e.g. yourname@gmail.com).";
        if (email.length() <= 10)
            return "Email is too short. Please enter a valid Gmail (e.g. yourname@gmail.com).";
        return null;
    }

    // ─────────────────────────────────────────
    // REGISTER
    // ─────────────────────────────────────────

    public void registerUser(String name, String email, String password, String role) {
        if (!isValidGmail(email)) {
            System.out.println(getEmailWarning(email));
            return;
        }
        repo.insertUser(name, email, PasswordUtil.hash(password), role);
    }

    // ─────────────────────────────────────────
    // LOGIN
    // ─────────────────────────────────────────

    public int loginUser(String email, String password) {
        if (!isValidGmail(email)) {
            System.out.println(getEmailWarning(email));
            return -1;
        }

        ResultSet rs = repo.getUserByEmail(email);
        try {
            if (rs != null && rs.next()) {
                String stored = rs.getString("password");
                String name   = rs.getString("name");
                if (PasswordUtil.verify(password, stored)) {
                    System.out.println("Login successful! Welcome, " + name + "!");
                    return rs.getInt("userId");
                } else {
                    System.out.println("Incorrect password.");
                }
            } else {
                System.out.println("User not found. Please register first.");
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
        }
        return -1;
    }

    // ─────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────

    public String getNameByEmail(String email) {
        ResultSet rs = repo.getUserByEmail(email);
        try {
            if (rs != null && rs.next()) return rs.getString("name");
        } catch (SQLException e) {
            System.out.println("Error fetching name: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
        }
        return "Staff";
    }

    public String getRoleByEmail(String email) {
        ResultSet rs = repo.getUserByEmail(email);
        try {
            if (rs != null && rs.next()) return rs.getString("role");
        } catch (SQLException e) {
            System.out.println("Error fetching role: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
        }
        return "staff";
    }

    // ─────────────────────────────────────────
    // ADMIN PASSWORD
    // ─────────────────────────────────────────

    public boolean adminPasswordExists() {
        return repo.getAdminPasswordHash() != null;
    }

    public void setAdminPassword(String newPassword) {
        repo.setAdminPasswordHash(PasswordUtil.hash(newPassword));
        System.out.println("Admin password set successfully.");
    }

    public boolean verifyAdminPassword(String input) {
        String stored = repo.getAdminPasswordHash();
        return stored != null && PasswordUtil.verify(input, stored);
    }
}
