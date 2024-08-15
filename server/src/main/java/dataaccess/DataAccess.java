package dataaccess;

import model.AuthData;
import model.UserData;
import model.GameData;

import java.util.Collection;


/**
 * Rpresents a class that can access data (operations to read/write data)
 */
public interface DataAccess {

    /**
     * Clears all data from the database
     * @throws DataAccessException if there is an error accessing the data
     */
    void clear() throws DataAccessException;

    /**
     * Persists a user to the database
     * @param user
     * @return
     * @throws DataAccessException
     */
    UserData writeUser(UserData user) throws DataAccessException;

    /**
     * Reads a user from the database
     * @param username
     * @return
     * @throws DataAccessException
     */
    UserData readUser(String username) throws DataAccessException;

    /**
     * Persists an authentication token to the database
     * @param username
     * @return
     * @throws DataAccessException
     */
    AuthData writeAuth(String username) throws DataAccessException;

    /**
     * Reads an authentication token from the database
     * @param token
     * @return
     * @throws DataAccessException
     */
    AuthData readAuth(String token) throws DataAccessException;

    /**
     * Deletes an authentication token from the database (idempotent)
     * @param token
     * @throws DataAccessException
     */
    void deleteAuth(String token) throws DataAccessException;

    /**
     * Creates a new game, gameID is assigned by the database
     * @param gameName name of the game
     * @return
     * @throws DataAccessException
     */
    GameData newGame(String gameName) throws DataAccessException;

    /**
     * Updates a game in the database
     * @param game
     * @throws DataAccessException
     */
    void updateGame(GameData game) throws DataAccessException;

    /**
     * Reads a game from the database
     * @param gameID
     * @return
     * @throws DataAccessException
     */
    GameData readGame(int gameID) throws DataAccessException;

    /**
     * Lists all games in the database
     * @return a collection of all games
     * @throws DataAccessException
     */
    Collection<GameData> listGames() throws DataAccessException;

}
