
package com.fashionstore.dao.impl;

import com.fashionstore.dao.UserDAO;
import com.fashionstore.model.User;
import com.fashionstore.util.DBConnection;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class UserDAOImpl implements UserDAO {

    private static final int ITERATIONS = 210000;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 256;

    private static final String INSERT_USER_SQL =
            "INSERT INTO users "
            + "(full_name, email, password, phone, address, city, state, postal_code) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String GET_USER_BY_ID_SQL =
            "SELECT * FROM users WHERE user_id = ?";

    private static final String GET_USER_BY_EMAIL_SQL =
            "SELECT * FROM users WHERE email = ?";

    private static final String GET_USER_BY_PHONE_SQL =
            "SELECT * FROM users WHERE phone = ?";

    private static final String GET_ALL_USERS_SQL =
            "SELECT * FROM users ORDER BY user_id";

    private static final String UPDATE_USER_SQL =
            "UPDATE users SET full_name = ?, email = ?, phone = ?, "
            + "address = ?, city = ?, state = ?, postal_code = ? "
            + "WHERE user_id = ?";

    private static final String UPDATE_PASSWORD_SQL =
            "UPDATE users SET password = ? WHERE user_id = ?";

    private static final String CHECK_EMAIL_SQL =
            "SELECT 1 FROM users WHERE email = ?";

    private static final String CHECK_PHONE_SQL =
            "SELECT 1 FROM users WHERE phone = ?";

    @Override
    public boolean registerUser(User user) throws SQLException {
        String hashedPassword = hashPassword(user.getPassword());

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, hashedPassword);
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getAddress());
            ps.setString(6, user.getCity());
            ps.setString(7, user.getState());
            ps.setString(8, user.getPostalCode());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getInt(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public User loginUser(String email, String password)
            throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_USER_BY_EMAIL_SQL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");

                    if (verifyPassword(password, storedHash)) {
                        return mapUser(rs);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public User getUserById(int userId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_USER_BY_ID_SQL)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }

        return null;
    }

    @Override
    public User getUserByEmail(String email) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_USER_BY_EMAIL_SQL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }

        return null;
    }

    @Override
    public User getUserByPhone(String phone) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_USER_BY_PHONE_SQL)) {

            ps.setString(1, phone);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(UPDATE_USER_SQL)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getAddress());
            ps.setString(5, user.getCity());
            ps.setString(6, user.getState());
            ps.setString(7, user.getPostalCode());
            ps.setInt(8, user.getUserId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updatePassword(int userId, String newPassword)
            throws SQLException {
        String hashedPassword = hashPassword(newPassword);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(UPDATE_PASSWORD_SQL)) {

            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean emailExists(String email) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(CHECK_EMAIL_SQL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean phoneExists(String phone) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(CHECK_PHONE_SQL)) {

            ps.setString(1, phone);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_ALL_USERS_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }

        return users;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();

        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));

        // Do not expose the stored password hash.
        user.setPassword(null);

        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setCity(rs.getString("city"));
        user.setState(rs.getString("state"));
        user.setPostalCode(rs.getString("postal_code"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        user.setCreatedAt(createdAt);

        return user;
    }

    private String hashPassword(String password) throws SQLException {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty.");
        }

        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(
                    "PBKDF2WithHmacSHA256");

            byte[] hash = factory.generateSecret(spec).getEncoded();

            return ITERATIONS + ":"
                    + Base64.getEncoder().encodeToString(salt) + ":"
                    + Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new SQLException("Password hashing failed.", e);
        } finally {
            spec.clearPassword();
        }
    }

    private boolean verifyPassword(String password, String storedHash)
            throws SQLException {
        if (password == null || storedHash == null) {
            return false;
        }

        String[] parts = storedHash.split(":");

        if (parts.length != 3) {
            return false;
        }

        try {
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[2]);

            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    iterations,
                    expectedHash.length * 8);

            try {
                SecretKeyFactory factory = SecretKeyFactory.getInstance(
                        "PBKDF2WithHmacSHA256");

                byte[] actualHash =
                        factory.generateSecret(spec).getEncoded();

                if (actualHash.length != expectedHash.length) {
                    return false;
                }

                int difference = 0;

                for (int i = 0; i < actualHash.length; i++) {
                    difference |= actualHash[i] ^ expectedHash[i];
                }

                return difference == 0;

            } finally {
                spec.clearPassword();
            }

        } catch (Exception e) {
            throw new SQLException("Password verification failed.", e);
        }
    }
}
