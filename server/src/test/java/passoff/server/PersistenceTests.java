package passoff.server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import passoff.exception.TestException;
import server.Server;

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
}
