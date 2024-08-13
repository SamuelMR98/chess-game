package websocket.commands;

import chess.ChessGame;

import static websocket.commands.UserGameCommand.CommandType.CONNECT;
public class JoinPlayerCommand extends GameCommand {
    public final ChessGame.TeamColor teamColor;

    public JoinPlayerCommand(String authToken, Integer gameId, ChessGame.TeamColor teamColor) {
        super(CONNECT, authToken, gameId);
        this.teamColor = teamColor;
    }
}
