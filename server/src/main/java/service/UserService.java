package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import spark.utils.StringUtils;
import util.CodedException;

import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    final private DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    /**
     * register a new user
     * @param user the user to register
     * @return the auth data for the new user
     * @throws CodedException
     */
    public AuthData registerUser(UserData user) throws CodedException {
        if (StringUtils.isEmpty(user.username()) || StringUtils.isEmpty(user.password())) {
            throw new CodedException(400, "Missing required fields");
        }

        try {
            var hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            var newUser = new UserData(user.username(), hashedPassword, user.email());

            newUser = dataAccess.writeUser(newUser);
            return dataAccess.writeAuth(newUser.username());
        } catch (DataAccessException e) {
            throw new CodedException(403, "Unable to register user");
        }
    }
}
