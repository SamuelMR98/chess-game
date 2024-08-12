package ui;

import chess.*;
import model.GameData;
import util.ExceptionUtil;
import util.ResponseException;

import java.util.Arrays;


public class ChessClient implements DisplayHandler {
    private GameData gameData;
    private GameData[] games;
    private String token;

    private State state = State.LOGGED_OUT;

    final private ServerFacade server;
    final private WebSocketFacade webSocket;

    public ChessClient(String hostname) throws Exception {
        server = new ServerFacade(hostname);
        webSocket = new WebSocketFacade(hostname, this);
    }

    public void clear() throws Exception {
        server.clear();
    }

    private String clear(String[] ignored) throws Exception {
        clear();
        state = State.LOGGED_OUT;
        gameData = null;
        return "Cleared";
    }

    private String quit(String[] ignored) {
        return "Quitting";
    }

    public String eval(String input) {
        var message = "Error with command. Try: Help";
        try {
            input = input.toLowerCase();
            var parts = input.split(" ");
            if(parts.length == 0) {
                parts = new String[] { "Help" };
            }
            var params = Arrays.copyOfRange(parts, 1, parts.length);
            try {
                message = (String) this.getClass().getDeclaredMethod(parts[0], String[].class).invoke(this, new Object[]{params});
            } catch (NoSuchMethodException e) {
                message = String.format("Unknown command: %s", help(params));
            }
        } catch (Throwable e) {
            var root = ExceptionUtil.getRootCause(e);
            message = String.format("Error: %s", root.getMessage());
        }
        return message;
    }

    private String help(String[] ingnored) {
        return switch (state) {
            case LOGGED_IN -> getHelp(loggedInHelp);
            case OBSERVING -> getHelp(observingHelp);
            case BLACK, WHITE -> getHelp(playingHelp);
            default -> getHelp(loggedOutHelp);
        };
    }

    private String login(String[] params) throws ResponseException {
        if (state == State.LOGGED_OUT && params.length == 2) {
            var res = server.login(params[0], params[1]);
            token = res.authToken();
            state = State.LOGGED_IN;
            return String.format("Logged in as %s", params[0]);
        }
        return "Failed to login";
    }

    private String register(String[] params) throws ResponseException {
        if (state == State.LOGGED_OUT && params.length == 3) {
            var res = server.register(params[0], params[1], params[2]);
            token = res.authToken();
            state = State.LOGGED_IN;
            return String.format("Registered as %s", params[0]);
        }
        return "Failed to register";
    }

    private String logout(String[] ignored) throws ResponseException {
        verifyAuth();

        if (state == State.LOGGED_IN) {
            server.logout(token);
            state = State.LOGGED_OUT;
            token = null;
            return "Logged out";
        }
        return "Failed to logout";
    }

    private String create(String[] params) throws ResponseException {
        verifyAuth();

        if (params.length == 1 && state == State.LOGGED_IN) {
            var gameData = server.createGame(token, params[0]);
            return String.format("Created game %s", gameData.gameID());
        }
        return "Failed to create game";
    }

    private void verifyAuth() throws ResponseException {
        if (token == null) {
            throw new ResponseException(401, "Please login (register) first");
        }
    }
}
