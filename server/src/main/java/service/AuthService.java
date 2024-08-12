package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import util.CodedException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AuthService {
    private final DataAccess dataAccess;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Create a new session for a user
     * @param user the user to create a session for
     * @return the auth data for the new session
     * @throws CodedException
     */
    public AuthData createSession(UserData user) throws CodedException {
        try {
            UserData existingUser = dataAccess.readUser(user.username());
            if (existingUser != null) {
                if (passwordEncoder.matches(user.password(), existingUser.password())) {
                    return dataAccess.writeAuth(user.username());
                }
            }
            throw new CodedException(401, "Invalid username or password");
        } catch (DataAccessException e) {
            throw new CodedException(500, "Internal server error");
        }
    }

    /**
     * Delete a session by token
     * @param token the token of the session to delete
     * @throws CodedException
     */
    public void deleteSession(String token) throws CodedException {
        try {
            dataAccess.deleteAuth(token);
        } catch (DataAccessException e) {
            throw new CodedException(500, "Internal server error");
        }
    }

    /**
     * Get the auth data for a session
     * @param token the token of the session
     * @return the auth data for the session
     * @throws CodedException
     */
    public AuthData getAuth(String token) throws CodedException {
        try {
            return dataAccess.readAuth(token);
        } catch (DataAccessException e) {
            throw new CodedException(500, "Internal server error");
        }
    }
}
