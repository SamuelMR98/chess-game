package ui;

import chess.ChessGame;

public enum State {
    LOGGED_OUT,
    LOGGED_IN,
    WHITE,
    BLACK,
    OBSERVING;

    public boolean isPlaying(ChessGame.TeamColor color) {
        return (color.toString().equals(this.toString()));
    }
}
