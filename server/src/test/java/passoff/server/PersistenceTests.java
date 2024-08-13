package passoff.server;

import org.junit.jupiter.api.*;
import passoff.model.*;
import passoff.exception.TestException;
import chess.ChessGame;
import server.Server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersistenceTests {
    private static TestServerFacade serverFacade;
    private static Server server;

    @BeforeAll
    public static void setup() {
        startServer();
        serverFacade.clear();
    }

    @AfterAll
    public static void teardown() {
        server.stop();
    }

    private static void startServer() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Test Server started on port " + port);

        serverFacade = new TestServerFacade("localhost", Integer.toString(port));
    }

    @Test
    @DisplayName("Test that the server can persist a user")
    public void persistenceTest() throws TestException {
        var initialRowCount = getDbRowCount();

        TestUser registerRequest = new TestUser("ExistingUser", "password", "existing@byu.edu");

        TestAuthResult regResult = serverFacade.register(registerRequest);
        var token = regResult.getAuthToken();

        // Create game
        var createRequest = new TestCreateGame(token, "Test Game");

        // join game
        TestJoinRequest joinRequest = new TestJoinRequest(ChessGame.TeamColor.WHITE, createResult.getGameID());
        serverFacade.joinPlayer(joinRequest, token);

        var newRowCount = getDbRowCount();
        Assertions.assertTrue(initialRowCount < newRowCount, "Database row count did not increase");

        // Test that the user can be retrieved after server restart
        teardown();
        startServer();
    
        TestListResult listResult = serverFacade.listGames(token);
        Assertions.assertEquals(200, serverFacade.getStatusCode(), "Server response code was not 200 OK");
        Assertions.assertEquals(1, listResult.getGames().length, "Missing game(s) in database after restart");

        TestListEntry game1 = listResult.getGames()[0];
        Assertions.assertEquals("Test Game", game1.getGameName(), "Game name does not match");
        Assertions.assertEquals("ExistingUser", game1.getWhiteUsername(), "White player does not match");
        Assertions.assertEquals(game1.getGameID(), createResult.getGameID(), "Game ID does not match");
        
        TestLoginRequest loginRequest = new TestLoginRequest("ExistingUser", "password");
        serverFacade.login(loginRequest);
        Assertions.assertEquals(200, serverFacade.getStatusCode(), "Server response code was not 200 OK");
    }

    private int getDbRowCount() throws TestException {
        int rows = 0;
        try {
            Class<?> clazz = Class.forName("dataAccess.DatabaseManager");
            var method = clazz.getDeclaredMethod("getConnection");
            method.setAccessible(true);

            Object o = clazz.getDeclaredConstructor().newInstance();
            try (Connection conn = (Connection) method.invoke(o)) {
                try (var stmt = conn.createStatement()) {
                    for (String table : getTables(conn)) {
                        var sql = "SELECT COUNT(*) FROM " + table;
                        try (var rs = stmt.executeQuery(sql)) {
                            if (rs.next()) {
                                rows += rs.getInt(1);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Assertions.fail("Unable to load db");
        }
        return rows;
    }

    private List<String> getTables(Connection conn) throws SQLException {
        String sql = """
                SELECT table_name
            FROM information_schema.tables
            WHERE table_schema = DATABASE();
            """;
        List<String> tableNames = new ArrayList<>();
        try (var stmt = conn.prepareStatement(sql)) {
            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tableNames.add(rs.getString(1));
                }
            }
        }
        return tableNames;
    }
}
