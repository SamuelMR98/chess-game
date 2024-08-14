package serviceTests;

import dataAccess.MemoryDataAccess;
import jdk.jfr.Description;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.AdminService;
import service.AuthService;
import service.GameService;
import service.UserService;
import util.CodedException;

public class AdminServiceTests {
    @Test
    @Description("Positive test case for clearing the database")
    public void clearDatabaseTest() throws CodedException {
        var memDataAccess = new MemoryDataAccess();
        var userService = new UserService(memDataAccess);
        var user = new UserData("John Doe", "J0hnD03", "jdoe@byu.edu");
        var authData = userService.registerUser(user);

        var gameService = new GameService(memDataAccess);
        gameService.createGame("TestGAme");

        var service = new AdminService(memDataAccess);
        Assertions.assertDoesNotThrow(service::clear);

        var authService = new AuthService(memDataAccess);
        Assertions.assertThrows(CodedException.class, () -> authService.createSession(user));
        Assertions.assertNull(authService.getAuth(authData.authToken()));

        var games = gameService.listGames();
        Assertions.assertEquals(0, games.size());
    }
}
