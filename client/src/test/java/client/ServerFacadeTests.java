package client;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;
import util.ResponseException;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearData() throws Exception {
        serverFacade.clear();
    }

    // Positive test for registration
    @Test
    @DisplayName("Positive Test: Register a new user")
    void registerPositive() throws Exception {
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");
        assertTrue(auth.authToken().length() > 10);
        assertEquals("John Doe", auth.username());
    }

    // Negative test for registration
    @Test
    @DisplayName("Negative Test: Register with an existing username")
    void registerNegative() throws Exception {
        serverFacade.register("John Doe", "password", "jdoe@byu.edu"); // First register
        assertThrows(ResponseException.class, () -> serverFacade.register("John Doe", "newpassword", "newemail@byu.edu"));
    }

    // Positive test for login/logout
    @Test
    @DisplayName("Positive Test: Login and Logout")
    void loginLogoutPositive() throws Exception {
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");
        serverFacade.logout(auth.authToken());
        var login = serverFacade.login("John Doe", "password");
        serverFacade.listGames(login.authToken());
        assertEquals("John Doe", login.username());
    }

    // Negative test for login/logout
    @Test
    @DisplayName("Negative Test: Login with incorrect credentials")
    void loginLogoutNegative() throws Exception {
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");
        serverFacade.logout(auth.authToken());
        assertThrows(ResponseException.class, () -> serverFacade.login("John Doe", "wrongpassword"));
    }

    // Positive test for creating a game
    @Test
    @DisplayName("Positive Test: Create a game")
    void createGamePositive() throws Exception {
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");
        var game = serverFacade.createGame(auth.authToken(), "Game 1");
        assertTrue(game.gameID() > 0);
        var games = serverFacade.listGames(auth.authToken());
        assertEquals(1, games.length);
        assertEquals("Game 1", games[0].gameName());
        game = serverFacade.joinGame(auth.authToken(), game.gameID(), ChessGame.TeamColor.WHITE);
        assertEquals(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                game.game().getBoard().getPiece(new ChessPosition(2, 1)));
    }

    // Negative test for creating a game
    @Test
    @DisplayName("Negative Test: Create a game with invalid token")
    void createGameNegative() throws Exception {
        assertThrows(ResponseException.class, () -> serverFacade.createGame("invalidToken", "Game 2"));
    }

    // Positive test for joining a game
    @Test
    @DisplayName("Positive Test: Join an existing game")
    void joinGamePositive() throws Exception {
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");
        var game = serverFacade.createGame(auth.authToken(), "Game 1");
        serverFacade.joinGame(auth.authToken(), game.gameID(), ChessGame.TeamColor.WHITE);
        var games = serverFacade.listGames(auth.authToken());
        assertEquals(1, games.length);
        assertEquals("Game 1", games[0].gameName());
        assertEquals(game.gameID(), games[0].gameID());
        assertEquals(auth.username(), games[0].whiteUsername());
    }

    // Negative test for joining a game
    @Test
    @DisplayName("Negative Test: Join a non-existent game")
    void joinGameNegative() throws Exception {
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");
        assertThrows(ResponseException.class, () -> serverFacade.joinGame(auth.authToken(), -1, ChessGame.TeamColor.BLACK));
    }

    // Positive test for listing games
    @Test
    @DisplayName("Positive Test: List games when there is one game")
    void listGamesPositive() throws Exception {
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");
        serverFacade.createGame(auth.authToken(), "Game 1");
        var games = serverFacade.listGames(auth.authToken());
        assertEquals(1, games.length);
        assertEquals("Game 1", games[0].gameName());
    }

    // Negative test for listing games
    @Test
    @DisplayName("Negative Test: List games with invalid token")
    void listGamesNegative() throws Exception {
        assertThrows(ResponseException.class, () -> serverFacade.listGames("invalidToken"));
    }

    // Positive test for clearing database
    @Test
    @DisplayName("Positive Test: Clear database and ensure it's empty")
    void clearPositive() throws Exception {
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");
        serverFacade.createGame(auth.authToken(), "Game 1");
        serverFacade.clear();
        assertThrows(ResponseException.class, () -> serverFacade.listGames(auth.authToken()));
    }

    // Negative test for clearing an already empty database
    @Test
    @DisplayName("Negative Test: Clear already empty database")
    void clearNegative() throws Exception {
        assertDoesNotThrow(() -> serverFacade.clear());
    }
}
