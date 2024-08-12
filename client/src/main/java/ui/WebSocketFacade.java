package ui;

import com.google.gson.Gson;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.GameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    DisplayHandler displayHandler;

    public WebSocketFacade(String serverName, DisplayHandler displayHandler) throws DeploymentException, IOException, URISyntaxException {
        var url = String.format("ws://%s/connect", serverName);
        URI socketURI = new URI(url);
        this.displayHandler = displayHandler;

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                var gson = new Gson();

                try {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getType()) {
                        case LOAD_GAME -> displayHandler.updateBoard(gson.fromJson(message, LoadMessage.class).gameData);
                        case ERROR -> displayHandler.error(gson.fromJson(message, ErrorMessage.class).errorMessage);
                        case NOTIFICATION -> displayHandler.message(gson.fromJson(message, NotificationMessage.class).notification);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                   System.out.println("Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {}

    public void sendCommand(GameCommand command) throws IOException {
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

}
