package websocket.commands;

public class GameCommand extends UserGameCommand {
    public final Integer gameID;

    public GameCommand(CommandType cmd, String authToken, Integer gameId) {
        super(authToken);
        this.commandType = cmd;
        this.gameID = gameId;
    }
}
