package dataaccess;

import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import dataAccess.MySqlDataAccess;
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
    @DisplayName("Write and Read User")
    public void writeReadUser(Class<? extends DataAccess> databaseClass) throws Exception {
        DataAccess dataAccess = startDataBase(databaseClass);
        var user = new UserData("John Doe", "password", "jdoe@byu.edu");

        var auth = dataAccess.writeAuth(user.username());
        Assertions.assertEquals(user.username(), auth.username());
        Assertions.assertFalse(auth.authToken().isEmpty());

        var returnedAuth = dataAccess.readAuth(auth.authToken());
        Assertions.assertEquals(user.username(), returnedAuth.username());
        Assertions.assertEquals(auth.authToken(), returnedAuth.authToken());

        var secondAuth = dataAccess.writeAuth(user.username());
        Assertions.assertNotEquals(auth.authToken(), secondAuth.authToken());
        Assertions.assertEquals(user.username(), secondAuth.username());
    }

    @ParameterizedTest
    @DisplayName("Write and Read Game")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    public void writeReadGame(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess db = startDataBase(dbClass);

        var game = db.newGame("blitz");
        var updatedGame = game.setBlack("joe");
        db.updateGame(updatedGame);

        var retrievedGame = db.readGame(game.gameID());
        Assertions.assertEquals(retrievedGame, updatedGame);
    }


    @ParameterizedTest
    @DisplayName("List Games")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    public void listGame(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess db = startDataBase(dbClass);

        var games = List.of(db.newGame("blitz"), db.newGame("fisher"), db.newGame("lightning"));
        var returnedGames = db.listGames();
        Assertions.assertIterableEquals(games, returnedGames);
    }
}
