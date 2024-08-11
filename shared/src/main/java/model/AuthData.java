package model;

import java.util.UUID;
import com.google.gson.Gson;

public record AuthData (String authToken, String username) {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
