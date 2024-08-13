import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;
import util.ResponseException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    @ DisplayName("Test register")
    void register() throws Exception {
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");
        assertTrue(auth.authToken().length() > 10);
    }

    @Test
    @DisplayName("Test login/logout")
    void loginLogout() throws Exception {
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");

        serverFacade.logout(auth.authToken());
        assertThrows(ResponseException.class, () -> serverFacade.listGames(auth.authToken()));

        var login = serverFacade.login("John Doe", "password");
        serverFacade.listGames(login.authToken());
    }

    @Test
    @DisplayName("Test create game")
    void createGame() throws Exception {
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");
        var game = serverFacade.createGame(auth.authToken(), "Game 1");

        assertTrue(game.gameID() > 0);

        game = serverFacade.joinGame(auth.authToken(), game.gameID(), ChessGame.TeamColor.WHITE);
        assertEquals(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), game.game().getBoard().getPiece(new ChessPosition(2, 1)));
    }

    @Test
    @DisplayName("Test join game")
    void joinGame() throws Exception {
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");
        var game = serverFacade.createGame(auth.authToken(), "Game 1");

        serverFacade.joinGame(auth.authToken(), game.gameID(), ChessGame.TeamColor.WHITE);

        var games = serverFacade.listGames(auth.authToken());
        assertEquals(1, games.length);
        assertEquals("Game 1", games[0].gameName());
        assertEquals(game.gameID(), games[0].gameID());
        assertEquals(auth.username(), games[0].whiteUsername());
    }

    @Test
    @DisplayName("Test list games")
    void listGames() throws Exception {
        var auth = serverFacade.register("John Doe", "password", "jdoe@byu.edu");
        var games = serverFacade.listGames(auth.authToken());
        assertEquals(0, games.length);

        serverFacade.createGame(auth.authToken(), "Game 1");

        games = serverFacade.listGames(auth.authToken());
        assertEquals(1, games.length);
        assertEquals("Game 1", games[0].gameName());
    }

}
