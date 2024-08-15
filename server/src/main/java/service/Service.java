package service;

import com.google.gson.Gson;
import spark.Request;
import util.CodedException;

import java.util.HashMap;
import java.util.Map;

import static server.Server.getString;

public class Service {
    <T> T getBody(Request request, Class<T> clazz) throws CodedException {
        var body = new Gson().fromJson(request.body(), clazz);
        if (body == null) {
            throw new CodedException(400, "Missing body");
        }
        return body;
    }

    String send(Object... props) {
        return getString(props);
    }
}
