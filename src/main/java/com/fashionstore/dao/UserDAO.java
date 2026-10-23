
package com.fashionstore.dao;

import com.fashionstore.model.User;
import java.sql.SQLException;
import java.util.List;

public interface UserDAO {

    boolean registerUser(User user) throws SQLException;

    User loginUser(String email, String password) throws SQLException;

    User getUserById(int userId) throws SQLException;

    User getUserByEmail(String email) throws SQLException;

    User getUserByPhone(String phone) throws SQLException;

    boolean updateUser(User user) throws SQLException;

    boolean updatePassword(int userId, String newPassword)
            throws SQLException;

    boolean emailExists(String email) throws SQLException;

    boolean phoneExists(String phone) throws SQLException;

    List<User> getAllUsers() throws SQLException;
}
