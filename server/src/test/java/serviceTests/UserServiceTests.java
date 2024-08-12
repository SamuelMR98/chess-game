package serviceTests;

import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.UserService;
import util.CodedException;

public class UserServiceTests {
    @Test
    public void registerUserTest() {
        var service = new UserService(new MemoryDataAccess());
        var user = new UserData("John Doe", "J0hnD03", "jdoe@byu.edu");

        Assertions.assertDoesNotThrow(() -> service.registerUser(user));
    }

    @Test
    public void duplicateUserTest() {
        var service = new UserService(new MemoryDataAccess());
        var user = new UserData("John Doe", "J0hnD03", "jdoe@byu.edu");

        Assertions.assertDoesNotThrow(() -> service.registerUser(user));
        Assertions.assertThrows(CodedException.class, () -> service.registerUser(user));
    }
}
