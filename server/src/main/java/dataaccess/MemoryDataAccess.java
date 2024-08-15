package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.UserData;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private int nextID = 1000;
    final private Map<String, UserData> users = new HashMap<>();
    final private Map<String, AuthData> auths = new HashMap<>();
    final private Map<Integer, GameData> games = new HashMap<>();

    public MemoryDataAccess() {}

    public void clear() {
        users.clear();
        auths.clear();
        games.clear();
    }

    public UserData writeUser(UserData user) throws DataAccessException {
        if (users.get(user.username()) == null) {
            users.put(user.username(), user);
            return user;
        }
        throw new DataAccessException("User already exists");
    }

    public UserData readUser(String username) {
        return users.get(username);
    }

    public AuthData writeAuth(String username) {
        var auth = new AuthData(AuthData.generateToken(), username);
        auths.put(auth.authToken(), auth);
        return auth;
    }

    public AuthData readAuth(String token) {
        return auths.get(token);
    }

    public void deleteAuth(String token) {
        auths.remove(token);
    }

    public GameData newGame(String gameName) {
        var gameID = nextID++;
        var game = new GameData(gameID, null, null, gameName, new ChessGame(), GameData.State.UNDECIDED);
        games.put(game.gameID(), game);
        game.game().getBoard().resetBoard();
        game.game().setTeamTurn(ChessGame.TeamColor.WHITE);
        return game;
    }

    public void updateGame(GameData game) {
        games.put(game.gameID(), game);
    }

    public GameData readGame(int gameID) {
        return games.get(gameID);
    }

    public Collection<GameData> listGames() {
        return games.values();
    }
}
