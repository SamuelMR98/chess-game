package websocket.messages;

import java.util.Objects;
public class ServerMessage {
    ServerMessageType type;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION,
    }

    public ServerMessage(ServerMessageType type) {
        this.type = type;
    }

    public ServerMessageType getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerMessage)) return false;
        ServerMessage that = (ServerMessage) o;
        return getType() == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType());
    }
}
