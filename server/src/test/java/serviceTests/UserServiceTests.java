package serviceTests;

import dataAccess.MemoryDataAccess;
import jdk.jfr.Description;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.UserService;
import util.CodedException;

public class UserServiceTests {
    @Test
    @Description("Positive test case for registering a user")
    public void registerUserTest() {
        var service = new UserService(new MemoryDataAccess());
        var user = new UserData("John Doe", "J0hnD03", "jdoe@byu.edu");

        Assertions.assertDoesNotThrow(() -> service.registerUser(user));
    }

    @Test
    @Description("Negative test case for registering a duplicate user")
    public void duplicateUserTest() {
        var service = new UserService(new MemoryDataAccess());
        var user = new UserData("John Doe", "J0hnD03", "jdoe@byu.edu");

        Assertions.assertDoesNotThrow(() -> service.registerUser(user));
        Assertions.assertThrows(CodedException.class, () -> service.registerUser(user));
    }

    @Test
    @Description("Negative test case for registering a user with missing fields")
    public void missingFieldsTest() {
        var service = new UserService(new MemoryDataAccess());
        var user = new UserData("John Doe", "", "jdoe@byu.edu");

        Assertions.assertThrows(CodedException.class, () -> service.registerUser(user));
    }

    @Test
    @Description("Negative test case for registering a user with a missing username")
    public void missingUsernameTest() {
        var service = new UserService(new MemoryDataAccess());
        var user = new UserData("", "J0hnD03", "jdoe@byu.edu");

        Assertions.assertThrows(CodedException.class, () -> service.registerUser(user));
    }
}
