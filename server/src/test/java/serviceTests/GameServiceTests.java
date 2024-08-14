package serviceTests;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import jdk.jfr.Description;
import model.GameData;
import org.junit.jupiter.api.Test;
import service.GameService;
import util.CodedException;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    @Test
    @Description("Positive test case for listing games")
    public void testListGames_Success() throws CodedException {

        ChessGame game1 = new ChessGame();
        ChessGame game2 = new ChessGame();
        var dataAccess = new MemoryDataAccess() {
            @Override
            public Collection<GameData> listGames() {
                return List.of(
                        new GameData(1, "white", "black", "Game1", game1, GameData.State.UNDECIDED),
                        new GameData(2, "White", "Black", "Game2", game2, GameData.State.UNDECIDED)
                );
            }

            // Other methods can be left unimplemented for this test
        };

        GameService gameService = new GameService(dataAccess);
        Collection<GameData> games = gameService.listGames();

        assertNotNull(games);
        assertEquals(2, games.size());
    }

    @Test
    @Description("Positive test case for creating a game")
    public void testCreateGame_Success() throws CodedException {
        ChessGame game1 = new ChessGame();
        var dataAccess = new MemoryDataAccess();


        GameService gameService = new GameService(dataAccess);
        GameData newGame = gameService.createGame("New Chess Game");

        assertNotNull(newGame);
        assertEquals("New Chess Game", newGame.gameName());
    }

    @Test
    @Description("Negative test case for joining a game with an unknown game")
    public void testJoinGame_UnknownGame() {
        DataAccess dataAccess = new MemoryDataAccess() {
            @Override
            public GameData readGame(int gameID) {
                return null;  // Simulate no game found for the given ID
            }

            // Other methods can be left unimplemented for this test
        };

        GameService gameService = new GameService(dataAccess);

        CodedException exception = assertThrows(CodedException.class, () -> {
            gameService.joinGame("user1", ChessGame.TeamColor.WHITE, 999);
        });

        assertEquals(400, exception.getStatusCode());
        assertEquals("Unknown game", exception.getMessage());
    }

    @Test
    public void testJoinGame_ColorAlreadyTaken() {
        DataAccess dataAccess = new MemoryDataAccess() {

            ChessGame game1 = new ChessGame();
            @Override
            public GameData readGame(int gameID) {
                // Simulate a game where the white color is already taken
                return new GameData(1, "user1", null, "Chess Game", game1, GameData.State.UNDECIDED);
            }

            @Override
            public void updateGame(GameData gameData) {
                // No action needed for this test
            }

            // Other methods can be left unimplemented for this test
        };

        GameService gameService = new GameService(dataAccess);

        CodedException exception = assertThrows(CodedException.class, () -> {
            gameService.joinGame("user1", ChessGame.TeamColor.WHITE, 1);
        });

        assertEquals(403, exception.getStatusCode());
        assertEquals("Color taken", exception.getMessage());
    }
}
