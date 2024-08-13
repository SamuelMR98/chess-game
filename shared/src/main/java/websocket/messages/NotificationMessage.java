package websocket.messages;

public class NotificationMessage extends SerializableServerMessage {
    public String notification;

    public NotificationMessage(String notification) {
        super(ServerMessageType.NOTIFICATION);
        this.notification = notification;
    }
}
