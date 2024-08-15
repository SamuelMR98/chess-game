package model;

import chess.ChessGame;
import com.google.gson.Gson;

public record GameData (int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game, State state) {
    public enum State {
        WHITE,
        BLACK,
        DRAW,
        UNDECIDED
    }

    public boolean isGameOver() {
        return state != State.UNDECIDED;
    }

    public GameData setWhite(String username) {
        return new GameData(this.gameID, username, this.blackUsername, this.gameName, this.game, this.state);
    }

    public GameData setBlack(String username) {
        return new GameData(this.gameID, this.whiteUsername, username, this.gameName, this.game, this.state);
    }

    /*
    public GameData clearBoard() {
        return new GameData(this.gameID, this.whiteUsername, this.blackUsername, this.gameName, null, this.state);
    }*/

    public GameData setState(State state) {
        return new GameData(this.gameID, this.whiteUsername, this.blackUsername, this.gameName, this.game, state);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
