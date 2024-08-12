package ui;

import chess.*;
import model.GameData;


public class ChessClient implements DisplayHandler {
    private GameData gameData;
    private GameData[] games;

    private State state = State.LOGGED_OUT;
}
