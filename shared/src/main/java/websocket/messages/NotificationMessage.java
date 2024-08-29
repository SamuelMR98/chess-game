package websocket.messages;

public class NotificationMessage extends SerializableServerMessage {
    public String message;

    public NotificationMessage(String notification) {
        super(ServerMessageType.NOTIFICATION);
        this.message = notification;
    }
}
