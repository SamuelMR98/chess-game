package dataaccess;

import model.*;
import chess.ChessGame;

import java.sql.*;
import java.util.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess {
    public MySqlDataAccess() throws DataAccessException {
        configureDB();
    }

    public void clear() throws DataAccessException {
        executeCmd("DELETE FROM `user`");
        executeCmd("DELETE FROM `authentication`");
        executeCmd("DELETE FROM `game`");
    }

    public UserData writeUser(UserData user) throws DataAccessException {
        if (user.username() != null) {
            var u = new UserData(user.username(), user.password(), user.email());
            executeUpdate("INSERT INTO `user` (username, password, email) VALUES (?, ?, ?)", u.username(), u.password(), u.email());
            return user;
        }
        return null;
    }

    public UserData readUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var stmt = conn.prepareStatement("SELECT password, email from `user` WHERE username = ?")) {
                stmt.setString(1, username);
                try (var rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        var password = rs.getString("password");
                        var email = rs.getString("email");
                        return new UserData(username, password, email);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error reading user: " + e.getMessage());
        }
        return null;
    }

    public AuthData writeAuth(String username) throws DataAccessException {
        var auth = new AuthData(AuthData.generateToken(), username);
        executeUpdate("INSERT INTO `authentication` (authToken, username) VALUES (?, ?)", auth.authToken(), auth.username());
        return auth;
    }

    public AuthData readAuth(String token) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var stmt = conn.prepareStatement("SELECT username from `authentication` WHERE authToken=?")) {
                stmt.setString(1, token);
                try (var rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(token, rs.getString("username"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error reading auth: " + e.getMessage());

        }
        return null;
    }

    public void deleteAuth(String token) throws DataAccessException {
        executeUpdate("DELETE FROM `authentication` WHERE authToken=?", token);
    }

    public GameData newGame(String gameName) throws DataAccessException {
        var game = new ChessGame();
        game.getBoard().resetBoard();
        var state = GameData.State.UNDECIDED;
        var gameID = executeUpdate("INSERT INTO `game` (gameName, whitePlayerName, blackPlayerName, game, state) VALUES (?, ?, ?, ?, ?)", gameName, null, null, game.toString(), state.toString());
        if (gameID != 0) {
            return new GameData(gameID, null, null, gameName, game, state);
        }
        return null;
    }

    public void updateGame(GameData gameData) throws DataAccessException {
        executeUpdate("UPDATE `game` set gameName=?, whitePlayerName=?, blackPlayerName=?, game=?, state=? WHERE gameID=?",
                gameData.gameName(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.game().toString(),
                gameData.state().toString(),
                gameData.gameID());
    }

    public GameData readGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var stmt = conn.prepareStatement("SELECT gameID, gameName, whitePlayerName, blackPlayerName, game, state FROM `game` WHERE gameID=?")) {
                stmt.setInt(1, gameID);
                try (var rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return readGameData(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error reading game: " + e.getMessage());
        }
        return null;
    }

    public Collection<GameData> listGames() throws DataAccessException {
        var games = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            try (var stmt = conn.prepareStatement("SELECT gameID, gameName, whitePlayerName, blackPlayerName, game, state FROM `game`")) {
                try (var rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        games.add(readGameData(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error reading games: " + e.getMessage());
        }
        return games;
    }

    // Helper methods for reading and writing to the database
    private GameData readGameData(ResultSet rs) throws SQLException {
        var gs = rs.getString("game");
        var gameID = rs.getInt("gameID");
        var gameName = rs.getString("gameName");
        var whitePlayerName = rs.getString("whitePlayerName");
        var blackPlayerName = rs.getString("blackPlayerName");
        var game = ChessGame.createGame(gs);
        var state = GameData.State.valueOf(rs.getString("state"));
        return new GameData(gameID, whitePlayerName, blackPlayerName, gameName, game, state);
    }

    private final String[] creteStatements = {
            """
            CREATE TABLE IF NOT EXISTS authentication (
              `authToken` varchar(100) NOT NULL,
              `username` varchar(100) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `gameName` varchar(45) DEFAULT NULL,
              `whitePlayerName` varchar(100) DEFAULT NULL,
              `blackPlayerName` varchar(100) DEFAULT NULL,
              `game` longtext NOT NULL,
              `state` varchar(45) DEFAULT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS user (
              `username` varchar(256) NOT NULL,
              `password` varchar(4096) NOT NULL,
              `email` varchar(45) NOT NULL,
              PRIMARY KEY (`username`),
              UNIQUE KEY `username_UNIQUE` (`username`)
            ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDB() throws DataAccessException {
        try {
            DatabaseManager.createDatabase();
            try (var conn = DatabaseManager.getConnection()) {
                for (var stmt : creteStatements) {
                    try (var preparedStatement = conn.prepareStatement(stmt)) {
                        preparedStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error configuring database: " + e.getMessage());
        }
    }

    private void executeCmd(String cmd) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var stmt = conn.prepareStatement(cmd)) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error executing command: " + e.getMessage());
        }
    }

    private int executeUpdate(String cmd, Object... args) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var stmt = conn.prepareStatement(cmd, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < args.length; i++) {
                    var param = args[i];
                    if (param instanceof String p) {
                        stmt.setString(i + 1, p);
                    } else if (param instanceof Integer p) {
                        stmt.setInt(i + 1, p);
                    } else if (param == null) {
                        stmt.setNull(i + 1, NULL);
                    }
                }
                stmt.executeUpdate();
                try (var rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error executing update: " + e.getMessage());
        }
        return 0;
    }

}
