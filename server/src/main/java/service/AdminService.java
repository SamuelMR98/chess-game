package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import util.CodedException;

/**
 * AdminService provides endpoints for the app admin
 */
public class AdminService {
    private final DataAccess dataAccess;

    public AdminService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    /**
     * Clears all data from the database (users, games, etc)
     *
     * @throws CodedException if there is an error clearing the data
     */
    // Endpoint for [DELETE] /db [200]{} [500]{"message": "Error: (description of error)"}
    public void clear() throws CodedException {
        try {
            dataAccess.clear();
        } catch (DataAccessException e) {
            throw new CodedException(500, "Server error");
        }
    }
}
