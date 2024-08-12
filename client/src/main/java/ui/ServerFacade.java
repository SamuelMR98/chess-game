package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import server.JoinRequest;
import util.ResponseException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;

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
    public AuthData register(String username, String password, String email) throws ResponseException {
        var req = Map.of(
            "username", username,
            "password", password,
            "email", email
        );
        return this.makeRequest("POST", "/user", req, null, AuthData.class);
    }

    public AuthData login(String username, String password) throws ResponseException {
        var req = Map.of(
            "username", username,
            "password", password
        );
        return this.makeRequest("POST", "/session", req, null, AuthData.class);
    }

    public void logout(String token) throws ResponseException {
        this.makeRequest("DELETE", "/session", null, token, null);
    }

    public void clear() throws ResponseException {
        this.makeRequest("DELETE", "/db", null, null, Map.class);
    }

    public GameData createGame(String token, String gameName) throws ResponseException {
        var req = Map.of(
            "gameName", gameName
        );
        return this.makeRequest("POST", "/game", req, token, GameData.class);
    }

    public GameData joinGame(String token, int gameId, ChessGame.TeamColor color) throws ResponseException {
        var req = new JoinRequest(color, gameId);
        this.makeRequest("PUT", "/game", req, token, GameData.class);
        return  getGame(token, gameId);
    }

    public GameData[] listGames(String token) throws ResponseException {
        record Response(GameData[] games) {}
        var res = this.makeRequest("GET", "/game", null, token, Response.class);
        return (res != null ? res.games : new GameData[0]);
    }

    private GameData getGame(String token, int gameId) throws ResponseException {
        var games = listGames(token);
        for (var game : games) {
            if (game.gameID() == gameId) {
                return game;
            }
        }
        throw new ResponseException(404, "Game not found");
    }

    // Helper method to make a request to the server
    private <T> T makeRequest(String method, String endpoint, Object req, String token, Class<T> clazz) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + endpoint)).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);

            if (token != null) {
                connection.addRequestProperty("Authorization", token);
            }

            if (req != null) {
                connection.addRequestProperty("Accept", "application/json");
                String reqData = new Gson().toJson(req);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = reqData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }
            connection.connect();

            try (InputStream resBody = connection.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(resBody, "utf-8");
                if (connection.getResponseCode() == 200) {
                    if (clazz != null) {
                        return new Gson().fromJson(reader, clazz);
                    }
                    return null;
                }
                throw new ResponseException(connection.getResponseCode(), reader);
            }
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
