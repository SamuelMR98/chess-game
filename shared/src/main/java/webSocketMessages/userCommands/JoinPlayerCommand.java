package webSocketMessages.userCommands;

import chess.ChessGame;

import static webSocketMessages.userCommands.UserGameCommand.CommandType.JOIN_PLAYER;
public class JoinPlayerCommand extends GameCommand {
    public final ChessGame.TeamColor teamColor;

    public JoinPlayerCommand(String authToken, Integer gameId, ChessGame.TeamColor teamColor) {
        super(JOIN_PLAYER, authToken, gameId);
        this.teamColor = teamColor;
    }
}
