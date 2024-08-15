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

    @Test
    @DisplayName("Test register")
    void register() throws Exception {
        // Positive test: Register a new user
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");
        assertTrue(auth.authToken().length() > 10);
        assertEquals("John Doe", auth.username());

        // Negative test: Register with an existing username
        assertThrows(ResponseException.class, () -> serverFacade.register("John Doe", "newpassword", "newemail@byu.edu"));
    }

    @Test
    @DisplayName("Test login/logout")
    void loginLogout() throws Exception {
        // Positive test: Register, login, and logout
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");

        serverFacade.logout(auth.authToken());
        assertThrows(ResponseException.class, () -> serverFacade.listGames(auth.authToken()));

        var login = serverFacade.login("John Doe", "password");
        serverFacade.listGames(login.authToken());
        assertEquals("John Doe", login.username());

        // Negative test: Login with incorrect credentials
        assertThrows(ResponseException.class, () -> serverFacade.login("John Doe", "wrongpassword"));
    }

    @Test
    @DisplayName("Test create game")
    void createGame() throws Exception {
        // Positive test: Create a game
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");
        var game = serverFacade.createGame(auth.authToken(), "Game 1");

        assertTrue(game.gameID() > 0);

        var games = serverFacade.listGames(auth.authToken());
        assertEquals(1, games.length);
        assertEquals("Game 1", games[0].gameName());

        game = serverFacade.joinGame(auth.authToken(), game.gameID(), ChessGame.TeamColor.WHITE);
        assertEquals(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                game.game().getBoard().getPiece(new ChessPosition(2, 1)));

        // Negative test: Create a game with an invalid token
        assertThrows(ResponseException.class, () -> serverFacade.createGame("invalidToken", "Game 2"));
    }

    @Test
    @DisplayName("Test join game")
    void joinGame() throws Exception {
        // Positive test: Join an existing game
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");
        var game = serverFacade.createGame(auth.authToken(), "Game 1");

        serverFacade.joinGame(auth.authToken(), game.gameID(), ChessGame.TeamColor.WHITE);

        var games = serverFacade.listGames(auth.authToken());
        assertEquals(1, games.length);
        assertEquals("Game 1", games[0].gameName());
        assertEquals(game.gameID(), games[0].gameID());
        assertEquals(auth.username(), games[0].whiteUsername());

        // Negative test: Join a non-existent game
        assertThrows(ResponseException.class, () -> serverFacade.joinGame(auth.authToken(), -1, ChessGame.TeamColor.BLACK));
    }

    @Test
    @DisplayName("Test list games")
    void listGames() throws Exception {
        // Positive test: List games when there is one game
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");
        serverFacade.createGame(auth.authToken(), "Game 1");

        var games = serverFacade.listGames(auth.authToken());
        assertEquals(1, games.length);
        assertEquals("Game 1", games[0].gameName());

        // Negative test: List games with an invalid token
        assertThrows(ResponseException.class, () -> serverFacade.listGames("invalidToken"));
    }

    @Test
    @DisplayName("Test clear")
    void clear() throws Exception {
        // Positive test: Clear database and ensure it's empty
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");
        serverFacade.createGame(auth.authToken(), "Game 1");

        serverFacade.clear();

        assertThrows(ResponseException.class, () -> serverFacade.listGames(auth.authToken()));

        // Negative test: Ensure clear doesn't throw an error when called on an already empty database
        assertDoesNotThrow(() -> serverFacade.clear());
    }
}
