package webSocketMessages.userCommands;

public class GameCommand extends UserGameCommand {
    public final Integer gameId;

    public GameCommand(CommandType cmd, String authToken, Integer gameId) {
        super(authToken);
        this.commandType = cmd;
        this.gameId = gameId;
    }
}
