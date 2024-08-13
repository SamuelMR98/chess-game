package passoff.server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import passoff.model.*;
import passoff.server.*;
import passoff.websocket.WebsocketTestingEnvironment;
import passoff.*;
import passoff.TestUtilities;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WebSocketTests {

    private static WebSocketTests webSocketTests;
    private static TestServerFacade serverFacade;
    private static Server server;
    private static Long time;

    private TestUser whiteUser;
    private TestUser blackUser;
    private TestUser spectatorUser;

    private Integer gameId;

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @BeforeAll
    public static void init() throws URISyntaxException {
        server = new Server();
        var port = Integer.toString(server.run(0));
        System.out.println("Test Server started on port " + port);

        serverFacade = new TestServerFacade("localhost", port);
        serverFacade.clear();

        environment = new WebSocketTestingEnvironment("localhost", port, "/connect");

        waitTime = TestUtilities.getMessageTime();
    }

    @BeforeEach
    public void setup() throws TestException {
        serverFacade.clear();

        whiteUser = new TestUser("whiteUser", "password", "wuser@byu.edu");
        blackUser = new TestUser("blackUser", "password", "buser@byu.edu");
        spectatorUser = new TestUser("spectatorUser", "password", "spectator@byu.edu");

        gameId = serverFacade.createGame(whiteUser, "Test Game");

        joinGame(whiteUser, gameId, ChessGame.TeamColor.WHITE);
        joinGame(blackUser, gameId, ChessGame.TeamColor.BLACK);
        joinGame(spectatorUser, gameId, ChessGame.TeamColor.SPECTATOR);
    }

    @AfterEach
    public void teardown() {
        environment.disconnect();
    }

    @Test
    @Order(1)
    @DisplayName("Join Player")
    public void goodJoinPlayer() {
        Map<String>, List<message>> messages = joinPlayer(whiteUser.username, whiteUser.authToken, gameId, ChessGame.TeamColor.WHITE, Set.of(), Set.of());

        assertLoadGameMessage(messages.get(whiteUser.username));

        messages = joinPlayer(blackUser.username, blackUser.authToken, gameId, ChessGame.TeamColor.BLACK, Set.of(whiteUser.username), Set.of());

        assertLoadGameMessage(messages.get(blackUser.username));
        assertNotificationMessage(messages.get(whiteUser.username));
    }

    @Test
    @Order(2)
    @DisplayName("Join Player Wrong Team")
    public void badJoinPlayer() {

    }

    @Test
    @Order(3)
    @DisplayName("Join Player Empty Team")
    public void emptyJoinPlayer() {
        TestCreateRequest createRequest = new TestCreateRequest("EmptyGame");
        TestCreateResult createResult = serverFacade.createGame(createRequest, whiteUser.authToken);

        Map<String, List<Message>> messages = joinPlayer(whiteUser.username, whiteUser.authToken, createResult.getGameID(), ChessGame.TeamColor.WHITE, Set.of(), Set.of());

        assertErrorMessage(messages.get(whiteUser.username))
    }

    @Test
    @Order(4)
    @DisplayName("Join Player Invalid GameId")
    public void joinPlayerInvalidGameId() {
        Map<String, List<Message>> messages = joinPlayer(whiteUser.username, whiteUser.authToken, -1, ChessGame.TeamColor.WHITE, Set.of(), Set.of());

        assertErrorMessage(messages.get(whiteUser.username));
    }

    @Test
    @Order(5)
    @DisplayName("Join Player Invalid AuthToken")
    public void joinPlayerInvalidAuthToken() {
        Map<String, List<Message>> messages = joinPlayer(whiteUser.username, "badToken", gameId, ChessGame.TeamColor.WHITE, Set.of(), Set.of());

        assertErrorMessage(messages.get(whiteUser.username));
    }

    @Test
    @Order(6)
    @DisplayName("Join as Spectator")
    public void joinSpectator() {
        // white player watch own game
        Map<String, List<Message>> messages = joinPlayer(whiteUser.username, whiteUser.authToken, gameId, ChessGame.TeamColor.SPECTATOR, Set.of(), Set.of());

        assertLoadGameMessage(messages.get(whiteUser.username));

        // black joins game
        messages = joinPlayer(blackUser.username, blackUser.authToken, gameId, ChessGame.TeamColor.BLACK, Set.of(whiteUser.username), Set.of());

        assertNotificationMessage(messages.get(whiteUser.username));

        // spectator joins game
        messages = joinSpectator(spectatorUser.username, spectatorUser.authToken, gameId, Set.of(whiteUser.username, blackUser.username), Set.of());

        assertLoadGameMessage(messages.get(spectatorUser.username));
        assertNotificationMessage(messages.get(whiteUser.username));
        assertNotificationMessage(messages.get(blackUser.username));


    }

    @Test
    @Order(7)
    @DisplayName("Join as Spectator Invalid GameId")
    public void joinSpectatorInvalidGameId() {
        Map<String, List<Message>> messages = joinSpectator(spectatorUser.username, spectatorUser.authToken, -1, Set.of(), Set.of());

        assertErrorMessage(messages.get(spectatorUser.username));
    }

    @Test
    @Order(8)
    @DisplayName("Join as Spectator Invalid AuthToken")
    public void joinSpectatorInvalidAuthToken() {
        Map<String, List<Message>> messages = joinSpectator(spectatorUser.username, "badToken", gameId, Set.of(), Set.of());

        assertErrorMessage(messages.get(spectatorUser.username));
    }

    @Test
    @Order(9)
    @DisplayName("Make a Move")
    public void validMove() {
        setUpNormalGame();

        ChessPosition start = TestUtilities.getChessPosition(2, 5);
        
    }


}
