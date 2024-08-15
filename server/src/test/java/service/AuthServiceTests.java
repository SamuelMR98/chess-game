package service;

import dataaccess.MemoryDataAccess;
import jdk.jfr.Description;
import org.junit.jupiter.api.Test;

import util.CodedException;
import model.UserData;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthServiceTests {
    @Test
    @Description("Positive test case for creating a session")
    public void testCreateSession_Success() throws CodedException {
        var memDataAccess = new MemoryDataAccess();
        var userService = new UserService(memDataAccess);
        var user = new UserData("John Doe", "J0hnD03", "jdoe@byu.edu");
        var authData = userService.registerUser(user);

        var authService = new AuthService(memDataAccess);
        assert(authData != null);

        assert(authService.createSession(user) != null);
        authService.deleteSession(authData.authToken());
    }

    @Test
    @Description("Negative test case for creating a session with an invalid password")
    public void testCreateSession_InvalidPassword() {
        var memDataAccess = new MemoryDataAccess();
        var authService = new AuthService(memDataAccess);
        var user = new UserData("John Doe", "J0hnD03", "jdoe@byu.edu");

        assertThrows(CodedException.class, () -> authService.createSession(user));

        var readAuthData = memDataAccess.readAuth(user.username());
        assert(readAuthData == null);
    }

    @Test
    @Description("Negative test case for creating a session with an invalid username")
    public void testCreateSession_InvalidUsername() throws CodedException {
        var memDataAccess = new MemoryDataAccess();
        var authService = new AuthService(memDataAccess);
        var user = new UserData("John Doe", "J0hnD03", "jdoe@byu.edu");

        assertThrows(CodedException.class, () -> authService.createSession(user));
    }

    @Test
    @Description("Positive test case for deleting a session")
    public void testDeleteSession_Success() throws CodedException {
        var memDataAccess = new MemoryDataAccess();
        var userService = new UserService(memDataAccess);
        var user = new UserData("John Doe", "J0hnD03", "jdoe@byu.edu");
        var authData = userService.registerUser(user);

        var authService = new AuthService(memDataAccess);

        assert (authData != null);

        authService.deleteSession(authData.authToken());

        var readAuthData = memDataAccess.readAuth(authData.authToken());
        assert (readAuthData == null);
    }

    @Test
    @Description("Negative test case for deleting a session with an invalid token")
    public void testDeleteSession_InvalidToken() throws CodedException {
        var memDataAccess = new MemoryDataAccess();
        var userService = new UserService(memDataAccess);
        var user = new UserData("John Doe", "J0hnD03", "jdoe@byu.edu");
        var authData = userService.registerUser(user);

        var authService = new AuthService(memDataAccess);
        assert(authData != null);

        authService.createSession(user);

        assert (authService.getAuth(authData.authToken()) != null);

        authService.deleteSession("invalidToken");

        assert (authService.getAuth(authData.authToken()) != null);

        authService.deleteSession(authData.authToken());

    }

}
