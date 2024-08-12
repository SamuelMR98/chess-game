package util;

import java.io.InputStreamReader;

import com.google.gson.Gson;
import java.util.HashMap;

public class ResponseException extends Exception {
    final private int code;

    public ResponseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ResponseException(int code, InputStreamReader reader) {
        super((String) (new Gson().fromJson(reader, HashMap.class)).get("message"));;
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
