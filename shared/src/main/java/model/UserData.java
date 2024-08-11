package model;

import com.google.gson.Gson;

/**
 * Represents a user's data (player or spectator)
 */

public record UserData (String username, String password, String email) {
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
