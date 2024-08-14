package service;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import util.CodedException;

import java.util.Collection;
import java.util.Objects;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    /**
     * List all games
     * @return a collection of all games
     * @throws CodedException if there is an error accessing the data
     */
    public Collection<GameData> listGames() throws CodedException{
        try {
            return dataAccess.listGames();
        } catch (DataAccessException e) {
            throw new CodedException(500, "Server error");
        }
    }

    /**
     * Create a new game
     * @param gameName the name of the game
     * @return the new game
     * @throws CodedException if there is an error accessing the data
     */
    public GameData createGame(String gameName) throws CodedException {
        try {
            return dataAccess.newGame(gameName);
        } catch (DataAccessException e) {
            throw new CodedException(500, "Server error");
        }
    }

    // Endpoint for [PUT] /game - Join Game - Body: { "color":"", "gameId":"" }
    // [200]{} [400]{"message": "Missing body"} [401]{"message": "Unauthorized"} [403]{"message": "Already taken"} [500]{"message": "Server error"}
    public GameData joinGame(String username, ChessGame.TeamColor color, int gameID) throws CodedException {
        try {
            var gameData = dataAccess.readGame(gameID);
            System.out.println(gameData);
            if (gameData == null) {
                throw new CodedException(400, "Unknown game");
            } else if (color == null) {
                throw new CodedException(400, "Missing color");
            } else if (gameData.isGameOver()) {
                throw new CodedException(403, "Game is over");
            } else {
                if (color == ChessGame.TeamColor.WHITE) {
                    if (gameData.whiteUsername() == null || gameData.whiteUsername().equals(username)) {
                        gameData = gameData.setWhite(username);
                    } else {
                        throw new CodedException(403, "Color taken");
                    }
                } else if (color == ChessGame.TeamColor.BLACK) {
                    if (gameData.blackUsername() == null || gameData.blackUsername().equals(username)) {
                        gameData = gameData.setBlack(username);
                    } else {
                        throw new CodedException(403, "Color taken");
                    }
                }
                dataAccess.updateGame(gameData);
            }
            return gameData;
        } catch (DataAccessException e) {
            throw new CodedException(500, "Server error");
        }
    }
}
