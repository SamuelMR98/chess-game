package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

public class DataAccessTests {
    private DataAccess startDataBase(Class<? extends DataAccess> databaseClass) throws Exception {
        DataAccess dataAccess = databaseClass.getDeclaredConstructor().newInstance();
        dataAccess.clear();
        return dataAccess;
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Positive Test: Write and Read User")
    public void writeReadUserPositive(Class<? extends DataAccess> databaseClass) throws Exception {
        DataAccess dataAccess = startDataBase(databaseClass);
        var user = new UserData("John Doe", "password", "jdoe@byu.edu");

        // Positive test: Write and read user
        var writtenUser = dataAccess.writeUser(user);
        Assertions.assertNotNull(writtenUser);
        Assertions.assertEquals(user.username(), writtenUser.username());

        var readUser = dataAccess.readUser(user.username());
        Assertions.assertNotNull(readUser);
        Assertions.assertEquals(user.username(), readUser.username());
        Assertions.assertEquals(user.password(), readUser.password());
        Assertions.assertEquals(user.email(), readUser.email());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Negative Test: Read Non-Existent User")
    public void writeReadUserNegative(Class<? extends DataAccess> databaseClass) throws Exception {
        DataAccess dataAccess = startDataBase(databaseClass);

        // Negative test: Read non-existent user
        var nonExistentUser = dataAccess.readUser("nonExistentUser");
        Assertions.assertNull(nonExistentUser);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Positive Test: Write and Read Auth")
    public void writeReadAuthPositive(Class<? extends DataAccess> databaseClass) throws Exception {
        DataAccess dataAccess = startDataBase(databaseClass);
        var user = new UserData("John Doe", "password", "jdoe@byu.edu");

        // Positive test: Write and read auth
        var auth = dataAccess.writeAuth(user.username());
        Assertions.assertNotNull(auth);
        Assertions.assertEquals(user.username(), auth.username());
        Assertions.assertFalse(auth.authToken().isEmpty());

        var returnedAuth = dataAccess.readAuth(auth.authToken());
        Assertions.assertNotNull(returnedAuth);
        Assertions.assertEquals(auth.authToken(), returnedAuth.authToken());
        Assertions.assertEquals(user.username(), returnedAuth.username());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Negative Test: Read Non-Existent Auth Token")
    public void writeReadAuthNegative(Class<? extends DataAccess> databaseClass) throws Exception {
        DataAccess dataAccess = startDataBase(databaseClass);

        // Negative test: Read non-existent auth token
        var nonExistentAuth = dataAccess.readAuth("invalidToken");
        Assertions.assertNull(nonExistentAuth);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Positive Test: Delete Auth")
    public void deleteAuthPositive(Class<? extends DataAccess> databaseClass) throws Exception {
        DataAccess dataAccess = startDataBase(databaseClass);
        var user = new UserData("John Doe", "password", "jdoe@byu.edu");

        // Positive test: Write and delete auth
        var auth = dataAccess.writeAuth(user.username());
        Assertions.assertNotNull(auth);

        dataAccess.deleteAuth(auth.authToken());
        var deletedAuth = dataAccess.readAuth(auth.authToken());
        Assertions.assertNull(deletedAuth);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Negative Test: Delete Non-Existent Auth Token")
    public void deleteAuthNegative(Class<? extends DataAccess> databaseClass) throws Exception {
        DataAccess dataAccess = startDataBase(databaseClass);

        // Negative test: Delete non-existent auth token
        dataAccess.deleteAuth("invalidToken");
        // Should not throw any exceptions, but assert anyway to ensure smooth operation
        Assertions.assertNull(dataAccess.readAuth("invalidToken"));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Positive Test: Write and Read Game")
    public void writeReadGamePositive(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess db = startDataBase(dbClass);

        // Positive test: Write and read game
        var game = db.newGame("blitz");
        var updatedGame = game.setBlack("joe");
        db.updateGame(updatedGame);

        var retrievedGame = db.readGame(game.gameID());
        Assertions.assertNotNull(retrievedGame);
        Assertions.assertEquals(retrievedGame, updatedGame);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Negative Test: Read Non-Existent Game")
    public void writeReadGameNegative(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess db = startDataBase(dbClass);

        // Negative test: Read non-existent game
        var nonExistentGame = db.readGame(-1);
        Assertions.assertNull(nonExistentGame);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Positive Test: List Games")
    public void listGames_Positive(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess db = startDataBase(dbClass);

        // Positive test: List games
        var games = List.of(db.newGame("blitz"), db.newGame("fisher"), db.newGame("lightning"));
        var returnedGames = db.listGames();
        Assertions.assertIterableEquals(games, returnedGames);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    @DisplayName("Negative Test: List Games When No Games Exist")
    public void listGamesNegative(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess db = startDataBase(dbClass);

        // Negative test: List games when no games exist
        db.clear(); // Clear database to ensure no games exist
        var emptyGameList = db.listGames();
        Assertions.assertTrue(emptyGameList.isEmpty());
    }
}
