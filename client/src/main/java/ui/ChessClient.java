package ui;

import chess.*;
import model.GameData;
import util.ExceptionUtil;
import util.ResponseException;
import websocket.commands.GameCommand;
import websocket.commands.JoinPlayerCommand;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;

import static util.EscapeSequences.*;


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

    private String list(String[] ignored) throws ResponseException {
        verifyAuth();

        games = server.listGames(token);
        StringBuilder sb = new StringBuilder();
        for (var i = 0; i < games.length; i++) {
            var game = games[i];
            sb.append(String.format("%d. %s white:%s black:%s state: %s%n", i, game.gameName(), game.whiteUsername(), game.blackUsername(), game.state()));
        }
        return sb.toString();
    }

    private String join(String[] params) throws Exception {
        verifyAuth();

        if (state == State.LOGGED_IN) {
            if (params.length == 2 && (params[1].equalsIgnoreCase("WHITE") || params[1].equalsIgnoreCase("BLACK"))) {
                var gamePosition = Integer.parseInt(params[0]);
                if (games != null && gamePosition >= 0 && gamePosition < games.length) {
                    var gameId = games[gamePosition].gameID();
                    var color = ChessGame.TeamColor.valueOf(params[1].toUpperCase());
                    var gameData = server.joinGame(token, gameId, color);
                    state = (color == ChessGame.TeamColor.WHITE ? State.WHITE : State.BLACK);
                    webSocket.sendCommand(new JoinPlayerCommand(token, gameId, color));

                    return String.format("Joined game %d as %s", gameData.gameID(), color);
                }
            }
        }

        return "Failed to join game";
    }

    private String observe(String[] params) throws ResponseException, IOException {
        verifyAuth();

        if (state == State.LOGGED_IN) {
            if (params.length == 1) {
                var gameId = Integer.parseInt(params[0]);
                var gameData = server.joinGame(token, gameId, null);
                state = State.OBSERVING;
                webSocket.sendCommand(new GameCommand(UserGameCommand.CommandType.JOIN_OBSERVER, token, gameId));
                return String.format("Observing game %d", gameData.gameID());
            }
        }
        return "Failed to observe game";
    }

    private String redraw(String[] ignored) throws Exception {
        verifyAuth();

        if (isPlaying() || isObserving()) {
            printGame();
            return "";
        }
        return "Failed to redraw";
    }

    private String legal(String[] params) throws Exception {
        verifyAuth();

        if (isPlaying() || isObserving()) {
            if (params.length == 1) {
                var pos = new ChessPosition(params[0]);
                var highlights = new ArrayList<ChessPosition>();
                highlights.add(pos);
                for (var move : gameData.game().validMoves(pos)) {
                    highlights.add(move.getEndPosition());
                }
                var color = state == State.BLACK ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
                printGame(color, highlights);
                return "";
            }
        }
        return "Failed to get legal moves";
    }

    String move(String[] params) throws Exception {
        verifyAuth();

        if (isTurn()) {
            if (params.length == 1) {
                var move = new ChessMove(params[0]);
                if (isMoveLegal(move)) {
                    webSocket.sendCommand(new MoveCommand(token, gameData.gameID(), move));
                    return "Moved";
                }
            }
        }
        return "Failed to move";
    }

    private String leave(String[] ignored) throws Exception {
        if (isPlaying() || isObserving()) {
            state = State.LOGGED_IN;
            webSocket.sendCommand(new GameCommand(UserGameCommand.CommandType.LEAVE, token, gameData.gameID()));
            gameData = null;
            return "Left game";
        }
        return "Failed to leave game";
    }

    private String resign(String[] ignored) throws Exception {
        if (isPlaying()) {
            webSocket.sendCommand(new GameCommand(UserGameCommand.CommandType.RESIGN, token, gameData.gameID()));
            state = State.LOGGED_IN;
            gameData = null;
            return "Resigned";
        }
        return "Failed to resign";
    }

    public boolean isMoveLegal(ChessMove move) {
        if (isTurn()) {
            var board = gameData.game().getBoard();
            var piece = board.getPiece(move.getStartPosition());
            if (piece != null) {
                var validMoves = piece.pieceMoves(board, move.getStartPosition());
                if (validMoves.contains(move)) {
                    return board.isLegalMove(move);
                }
            }
        }
        return false;
    }

    // Print function helpers
    private void printGame() {
        var color = state == State.BLACK ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        printGame(color, null);
    }
    private void printGame(ChessGame.TeamColor color, Collection<ChessPosition> highlights) {
        System.out.println("\n");
        System.out.print((gameData.game().getBoard()).toString(color, highlights));
        System.out.println();
    }

    public void printPrompt() {
        String gameState = "Not playing";
        if (gameData != null) {
            gameState = switch (gameData.state()) {
                case UNDECIDED -> String.format("%s's turn", gameData.game().getTeamTurn());
                case DRAW -> "Draw";
                case BLACK -> "Black won";
                case WHITE -> "White Won";
            };
        }
        System.out.print(RESET_TEXT_COLOR + String.format("\n[%s: %s] >>> ", state, gameState) + SET_TEXT_COLOR_GREEN);
    }

    public boolean isPlaying() {
        return (gameData != null && (state == State.WHITE || state == State.BLACK) && !isGameOver());
    }


    public boolean isObserving() {
        return (gameData != null && (state == State.OBSERVING));
    }

    public boolean isGameOver() {
        return (gameData != null && gameData.isGameOver());
    }

    public boolean isTurn() {
        return (isPlaying() && state.isPlaying(gameData.game().getTeamTurn()));
    }


    @Override
    public void updateBoard(GameData newGameData) {
        gameData = newGameData;
        printGame();
        printPrompt();

        if (isGameOver()) {
            state = State.LOGGED_IN;
            printPrompt();
            gameData = null;
        }
    }
    @Override
    public void message(String message) {
        System.out.println();
        System.out.println(SET_TEXT_COLOR_MAGENTA + "NOTIFY: " + message);
        printPrompt();
    }

    @Override
    public void error(String message) {
        System.out.println();
        System.out.println(SET_TEXT_COLOR_RED + "NOTIFY: " + message);
        printPrompt();

    }

    /**
     * Help commands and it's color and description
     */
    private record Help(String cmd, String description) {}

    private final List<Help> loggedOutHelp = List.of(
            new Help("register <USERNAME> <PASSWORD> <EMAIL>", "to create an account"),
            new Help("login <USERNAME> <PASSWORD>", "to play chess"),
            new Help("quit", "playing chess"),
            new Help("help", "with possible commands")
    );

    static final List<Help> loggedInHelp = List.of(
            new Help("create <NAME>", "a game"),
            new Help("list", "games"),
            new Help("join <ID> [WHITE|BLACK]", "a game"),
            new Help("observe <ID>", "a game"),
            new Help("logout", "when you are done"),
            new Help("quit", "playing chess"),
            new Help("help", "with possible commands")
    );

    static final List<Help> observingHelp = List.of(
            new Help("legal", "moves for the current board"),
            new Help("redraw", "the board"),
            new Help("leave", "the game"),
            new Help("quit", "playing chess"),
            new Help("help", "with possible commands")
    );

    static final List<Help> playingHelp = List.of(
            new Help("redraw", "the board"),
            new Help("leave", "the game"),
            new Help("move <crcr> [q|r|b|n]", "a piece with optional promotion"),
            new Help("resign", "the game without leaving it"),
            new Help("legal <cr>", "moves a given piece"),
            new Help("quit", "playing chess"),
            new Help("help", "with possible commands")
    );

    private String getHelp(List<Help> help) {
        var sb = new StringBuilder();
        for (var h : help) {
            sb.append(String.format("  %s%s%s - %s%s%s%n", SET_TEXT_COLOR_BLUE, h.cmd, RESET_TEXT_COLOR, SET_TEXT_COLOR_MAGENTA, h.description, RESET_TEXT_COLOR));
        }
        return sb.toString();
    }
    private void verifyAuth() throws ResponseException {
        if (token == null) {
            throw new ResponseException(401, "Please login (register) first");
        }
    }
}
