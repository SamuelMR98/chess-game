package webSocketMessages.serverMessages;

import model.GameData;

public class LoadMessage extends SerializableServerMessage {
    public GameData gameData;

    public LoadMessage(GameData gameData) {
        super(ServerMessageType.LOAD_GAME);
        this.gameData = gameData;
    }
}
