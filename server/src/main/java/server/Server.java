package server;

import com.google.gson.Gson;
import dataAccess.MySqlDataAccess;
import model.*;
import service.*;
import spark.*;
import util.CodedException;

import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;


import static spark.Spark.webSocket;

public class Server {

    UserService userService;
    GameService gameService;
    AdminService adminService;
    AuthService authService;
    WebSocketHandler webSocketHandler;

    public static final Logger log = Logger.getLogger("Chess Server");
    public int run(int desiredPort) {
        try {
            loadServices();

            Spark.port(desiredPort);

            Spark.staticFiles.location("web");

            webSocket("/connect", webSocketHandler);

            // Register your endpoints and handle exceptions here.
            Spark.delete("/db", this::clearApplication);
            Spark.post("/user", this::registerUser);
            Spark.post("/session", this::createSession);
            Spark.delete("/session", this::deleteSession);
            Spark.get("/game", this::listGames);
            Spark.post("/game", this::createGame);
            Spark.put("/game", this::joinGame);
            Spark.afterAfter(this::log);

            Spark.exception(CodedException.class, this::errorHandler);
            Spark.exception(Exception.class, (e, req, res) -> errorHandler(new CodedException(500, e.getMessage()), req, res));
            Spark.notFound((req, res) -> {
                var msg = String.format("[%s] %s not found", req.requestMethod(), req.pathInfo());
                return errorHandler(new CodedException(404, msg), req, res);
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        //This line initializes the server and can be removed once you have a functioning endpoint
        //Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    /**
     * loads the multiple services for a chess server
     * @throws Exception
     */
    private void loadServices() throws Exception{
        var dataAccess = new MySqlDataAccess();

        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        adminService = new AdminService(dataAccess);
        authService = new AuthService(dataAccess);
        webSocketHandler = new WebSocketHandler(dataAccess);
    }

    private Object errorHandler(CodedException e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        res.type("application/json");
        res.status(e.getStatusCode());
        res.body(body);
        return body;
    }

    private void log(Request req, Response res) {
        //log.info(String.format("[%s] %s %s", req.ip(), req.requestMethod(), req.pathInfo()));
    }

    /**
     * DELETE /db endpoint handler
     * @param req the request
     * @param res the response
     * @return the response body
     * @throws CodedException if there is an error clearing the database
     */
    // Endpoint for [DELETE] /db [200]{} [500]{"message": "Error: (description of error)"}
    private Object clearApplication(Request req, Response res) throws CodedException {
        adminService.clear();
        return send();
    }

    /**
     * POST /user endpoint handler
     * @param req the request
     * @param res the response
     * @return the response body
     * @throws CodedException if there is an error registering the user
     */
    // Endpoint for [POST] /user - Register User - Body: { "username":"", "password":"", "email":"" }
    // [200]{"username":"", "authToken":""} [400]{"message": "Missing required fields"}
    private Object registerUser(Request req, Response res) throws CodedException {
        var user = getBody(req, UserData.class);
        var auth = userService.registerUser(user);
        return send("username", user.username(), "authToken", auth.authToken());
    }

    /**
     * POST /session endpoint handler
     * @param req the request
     * @param res the response
     * @return the response body
     * @throws CodedException if there is an error creating the session
     */
    public Object createSession(Request req, Response res) throws CodedException {
        var user = getBody(req, UserData.class);
        var auth = authService.createSession(user);
        return send("username", user.username(), "authToken", auth.authToken());
    }

    /**
     * DELETE /session endpoint handler
     * @param req the request
     * @param res the response
     * @return the response body
     * @throws CodedException if there is an error deleting the session
     */
    public Object deleteSession(Request req, Response res) throws CodedException {
        var token = unauthorized(req);
        authService.deleteSession(token.authToken());
        return send();
    }

    /**
     * GET /game endpoint handler
     * @param req the request
     * @param res the response
     * @return the response body
     * @throws CodedException if there is an error listing the games
     */
    public Object listGames(Request req, Response res) throws CodedException {
        unauthorized(req);
        var games = gameService.listGames();
        return send("games", games.toArray());
    }

    /**
     * POST /game endpoint handler
     * @param req the request
     * @param res the response
     * @return the response body
     * @throws CodedException if there is an error creating the game
     */
    public Object createGame(Request req, Response res) throws CodedException {
        unauthorized(req);
        var gameData = getBody(req, GameData.class);
        gameData = gameService.createGame(gameData.gameName());
        return send("gameID", gameData.gameID());
    }

    /**
     * PUT / endpoint handler
     * @param req the request
     * @param res the response
     * @return the response body
     * @throws CodedException if there is an error joining the game
     */
    // Endpoint for [PUT] /game - Join Game - Body: { "color":"", "gameId":"" }
    // [200]{} [400]{"message": "Missing body"} [401]{"message": "Unauthorized"} [403]{"message": "Already taken"} [500]{"message": "Server error"}
    public Object joinGame(Request req, Response res) throws CodedException {
        var token = unauthorized(req);
        var joinRequest = getBody(req, JoinRequest.class);
        var gameData = gameService.joinGame(token.username(), joinRequest.playerColor(), joinRequest.gameID());
        return send();
    }

    // Helper methods
    private <T> T getBody(Request request, Class<T> clazz) throws CodedException {
        var body = new Gson().fromJson(request.body(), clazz);
        if (body == null) {
            throw new CodedException(400, "Missing body");
        }
        return body;
    }

    private String send(Object... props) {
        Map<Object, Object> map = new HashMap<>();
        for (var i = 0; i + 1 < props.length; i = i + 2) {
            map.put(props[i], props[i + 1]);
        }
        return new Gson().toJson(map);
    }

    private AuthData unauthorized(Request req) throws CodedException {
        var token = req.headers("authorization");
        if (token != null) {
            var authData = authService.getAuth(token);
            if (authData != null) {
                return authData;
            }
        }
        throw new CodedException(401, "Unauthorized");
    }
}
