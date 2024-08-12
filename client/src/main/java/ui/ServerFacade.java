package ui;

import model.AuthData;

/**
 * A facade for the server
 */
public class ServerFacade {
    private  final String serverUrl;

    public ServerFacade(String serverAddress) {
        serverUrl = String.format("http://%s", serverAddress);
    }

    public ServerFacade(int port) {
        serverUrl = String.format("http://localhost:%d", port);
    }

    // Endpoints for the server in the client
    public AuthData register(String username, String password, String email) {

        return null;
    }
}
