package service;

import dao.UserDAO;
import model.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthService {
    private UserDAO userDAO = new UserDAO();

    public User login(String email, String password) {
        String hashedPass = hashPassword(password);
        return userDAO.authenticate(email, hashedPass);
    }

    public boolean register(String email, String password, int roleId, int deptId) {
        String hashedPass = hashPassword(password);
        return userDAO.registerUser(email, hashedPass, roleId, deptId);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}