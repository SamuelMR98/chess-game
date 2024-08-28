package server;

import util.StringUtil;

import dataaccess.*;
import model.*;
import chess.*;
import com.google.gson.Gson;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.messages.*;
import websocket.commands.*;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {
    private final DataAccess dataAccess;

    public static class Connection {
        public UserData user;
        public GameData game;
        public Session session;

        public Connection(UserData user, Session session) {
            this.user = user;
            this.session = session;
        }

        private void send(String msg) throws Exception {
            session.getRemote().sendString(msg);
        }

        private void sendError(String msg) throws Exception {
            sendError(session.getRemote(), msg);
        }

        private static void sendError(RemoteEndpoint endpoint, String msg) throws Exception {
            var errMsg = (new ErrorMessage(String.format("ERROR: %s", msg))).toString();
            endpoint.sendString(errMsg);
        }

    }

    public static class ConnectionManager {
        public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

        public void add(String username, Connection connection) {
            connections.put(username, connection);
        }

        public Connection get(String username) {
            return connections.get(username);
        }

        public void remove(Session session) {
            Connection removeConnection = null;
            for (var c : connections.values()) {
                if (c.session.equals(session)) {
                    removeConnection = c;
                    break;
                }
            }

            if (removeConnection != null) {
                connections.remove(removeConnection.user.username());
            }
        }

        public void broadcast(int gameID, String excludeUsername, String msg) throws Exception {
            var removeList = new ArrayList<Connection>();
            for (var c : connections.values()) {
                if (c.session.isOpen()) {
                    if (c.game.gameID() == gameID && !StringUtil.isEqual(c.user.username(), excludeUsername)) {
                        c.send(msg);
                    }
                } else {
                    removeList.add(c);
                }
            }
            for (var c : removeList) {
                connections.remove(c.user.username());
            }
        }

        @Override
        public String toString() {
            var sb = new StringBuilder("[\n");
            for (var c : connections.values()) {
                sb.append(String.format("  {'game':%d, 'user': %s}%n", c.game.gameID(), c.user));
            }
            sb.append("]");
            return sb.toString();
        }
    }

    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
    }


    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        connections.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        try {
            var command = readJson(message, GameCommand.class);
            var connection = getConnection(command.getAuthString(), session);
            if (connection != null) {
                switch (command.getCommandType()) {
                    case CONNECT -> join(connection, readJson(message, JoinPlayerCommand.class));
                    case MAKE_MOVE -> move(connection, readJson(message, MoveCommand.class));
                    case LEAVE -> leave(connection, command);
                    case RESIGN -> resign(connection, command);
                }
            } else {
                Connection.sendError(session.getRemote(), "unknown user");
            }
        } catch (Exception e) {
            Connection.sendError(session.getRemote(), util.ExceptionUtil.getRootCause(e).getMessage());
        }
    }

    /**
     * Handles a player joining a game
     * @param connection the connection
     * @param command the command
     * @throws Exception if there is an error joining the game
     */
    private void join(Connection connection, JoinPlayerCommand command) throws Exception {
        var gameData = dataAccess.readGame(command.gameId);
        if (gameData != null) {
            var expectedUsername = (command.teamColor == BLACK) ? gameData.blackUsername() : gameData.whiteUsername();
            if (StringUtil.isEqual(expectedUsername, connection.user.username())) {
                connection.game = gameData;
                var loadMsg = (new LoadMessage(gameData)).toString();
                connection.send(loadMsg);

                var notificationMsg = (new NotificationMessage(String.format("%s joined %s as %s", connection.user.username(),
                        gameData.gameName(), command.teamColor))).toString();
                connections.broadcast(gameData.gameID(), connection.user.username(), notificationMsg);
            } else {
                connection.sendError("player has not joined game");
            }
        } else {
            connection.sendError("game not found");
        }
    }
    /**
     * Handles a user observing a game
     * @param connection
     * @param command
     * @throws Exception
     */
    private void observe(Connection connection, GameCommand command) throws Exception {
        var gameData = dataAccess.readGame(command.gameId);
        if (gameData != null) {
            connection.game = gameData;
            var loadMsg = (new LoadMessage(gameData)).toString();
            connection.send(loadMsg);

            var notificationMsg = (new NotificationMessage(String.format("%s is observing the game", connection.user.username()))).toString();
            connections.broadcast(gameData.gameID(), connection.user.username(), notificationMsg);
        } else {
            connection.sendError("game not found");
        }
    }
    /**
     * Handles the move command
     * @param connection the connection
     * @param command the command
     * @throws Exception if there is an error moving
     */
    private void move (Connection connection, MoveCommand command) throws Exception {
        var gameData = dataAccess.readGame(command.gameId);
        if (gameData != null) {
            if (!gameData.isGameOver()) {
                if (isTurn(gameData, command.move, connection.user.username())) {
                    gameData.game().makeMove(command.move);
                    var notificationMsg = (new NotificationMessage(String.format("%s moved %s",
                            connection.user.username(), command.move))).toString();
                    connections.broadcast(gameData.gameID(), connection.user.username(), notificationMsg);

                    gameData = handleGameState(gameData);
                    dataAccess.updateGame(gameData);
                    connection.game = gameData;

                    var loadMsg = (new LoadMessage(gameData)).toString();
                    connections.broadcast(gameData.gameID(), "", loadMsg);
                } else {
                    connection.sendError("invalid move: " + command.move);
                }
            } else {
                connection.sendError("game is over: " + gameData.state());
            }
        } else {
            connection.sendError("unknown game");
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * function to resign
     * @param connection the connection
     * @param command the command
     * @throws Exception if there is an error resigning
     */
    private void resign(Connection connection, GameCommand command) throws Exception {
        var gameData = dataAccess.readGame(command.gameId);
        if (gameData != null && !gameData.isGameOver()) {
            var playerColor = getPlayerColor(gameData, connection.user.username());
            if (playerColor != null) {
                var state = playerColor == ChessGame.TeamColor.WHITE ? GameData.State.BLACK : GameData.State.WHITE;
                gameData = gameData.setState(state);
                dataAccess.updateGame(gameData);
                connection.game = gameData;

                var notificationMessage = (new NotificationMessage(String.format("%s resigned", connection.user.username()))).toString();
                connections.broadcast(gameData.gameID(), "", notificationMessage);
            } else {
                connection.sendError("only players can resign");
            }
        } else {
            connection.sendError("game not found");
        }
    }

    /**
     * Handles user leaving the game
     * @param connection the connection
     * @param command the command
     * @throws Exception if there is an error leaving the game
     */
    private void leave(Connection connection, GameCommand command) throws Exception {
        var gameData = dataAccess.readGame(command.gameId);
        if (gameData != null) {
            if (StringUtil.isEqual(gameData.blackUsername(), connection.user.username())) {
                gameData = gameData.setBlack(null);
            } else if (StringUtil.isEqual(gameData.whiteUsername(), connection.user.username())) {
                gameData = gameData.setWhite(null);
            }
            dataAccess.updateGame(gameData);
            connections.remove(connection.session);
            var notificationMessage = (new NotificationMessage(String.format("%s left the game", connection.user.username()))).toString();
            connections.broadcast(gameData.gameID(), "", notificationMessage);
        } else {
            connection.sendError("game not found");
        }
    }

    /**
     * Gets the player color for the current game
     * @param gameData the game data
     * @param username the username
     * @return the player color
     */
    private ChessGame.TeamColor getPlayerColor(GameData gameData, String username) {
        if (StringUtil.isEqual(gameData.blackUsername(), username)) {
            return BLACK;
        } else if (StringUtil.isEqual(gameData.whiteUsername(), username)) {
            return WHITE;
        }
        return null;
    }

    /**
     * return if is my turn
     * @param gameData the game data
     * @param move the move
     * @param username the username
     * @return if is my turn
     */
    private boolean isTurn(GameData gameData, ChessMove move, String username) {
        var piece = gameData.game().getBoard().getPiece(move.getStartPosition());
        var turn = gameData.game().getTeamTurn();
        var turnUsername = turn.equals(WHITE) ? gameData.whiteUsername() : gameData.blackUsername();
        return (turnUsername.equals(username) && piece != null && piece.getTeamColor().equals(turn));
    }

    /**
     * Handles the game state
     * @param gameData
     * @return the game data
     * @throws Exception
     */
    private GameData handleGameState(GameData gameData) throws Exception {
        NotificationMessage notificationMessage = null;
        var game = gameData.game();
        if (game.isInStalemate(WHITE) || game.isInStalemate(BLACK)) {
            gameData = gameData.setState(GameData.State.DRAW);
            notificationMessage = new NotificationMessage("Game Over (Stalemate) The game is a draw");
        } else if (game.isInCheckmate(WHITE)) {
            gameData = gameData.setState(GameData.State.BLACK);
            notificationMessage = new NotificationMessage(String.format("Game Over, Black player, %s, wins!", gameData.blackUsername()));
        } else if (game.isInCheckmate(BLACK)) {
            gameData = gameData.setState(GameData.State.WHITE);
            notificationMessage = new NotificationMessage(String.format("Game Over, White player, %s, wins!", gameData.whiteUsername()));
        } else if (game.isInCheck(WHITE)) {
            notificationMessage = new NotificationMessage(String.format("Check, White player %s is in check", gameData.whiteUsername()));
        } else if (game.isInCheck(BLACK)) {
            notificationMessage = new NotificationMessage(String.format("Check, Black player %s is in check", gameData.blackUsername()));
        }

        if (notificationMessage != null) {
            connections.broadcast(gameData.gameID(), "", notificationMessage.toString());
        }
        return gameData;
    }

    /**
     * get the connection
     * @param id
     * @param session
     * @return the connection
     * @throws DataAccessException
     */
    private Connection getConnection(String id, Session session) throws DataAccessException {
        Connection connection = null;
        var authData = isAuthorized(id);
        if (authData != null) {
            connection = connections.get(authData.username());
            if (connection == null) {
                var user = dataAccess.readUser(authData.username());
                connection = new Connection(user, session);
                connections.add(authData.username(), connection);
            }
        }
        return connection;
    }

    /**
     * checks if the user is authorized
     * @param token
     * @return
     * @throws DataAccessException
     */
    public AuthData isAuthorized(String token) throws DataAccessException {
        if (token != null) {
            return dataAccess.readAuth(token);
        }
        return null;
    }

    private static <T> T readJson(String json, Class<T> clazz) throws IOException {
        var gson = new Gson();
        var obj = gson.fromJson(json, clazz);
        if (obj == null) {
            throw new IOException("Invalid JSON");
        }
        return obj;
    }
}
