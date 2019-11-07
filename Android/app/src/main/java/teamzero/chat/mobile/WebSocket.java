package teamzero.chat.mobile;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocket {

    private WebSocketClient mWebSocketClient;
    private String response;

    public WebSocket() {
        connectWebSocket();
    }

    private void connectWebSocket() {

        URI uri;
        try {
            // Used the following URI for testing purposes only
            // TODO: Change URI to Heroku server
            uri = new URI("ws://echo.websocket.org:80/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {

            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
            }

            @Override
            public void onMessage(String message) {
                Log.i("Websocket", "Received message = " + message);
                response = message;
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };

        mWebSocketClient.connect();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getResponse() {
        return response;
    }

    public void closeConnection() {
        if(!mWebSocketClient.getConnection().isClosed())
            mWebSocketClient.close();
    }

    public void sendMessage(String message) {
        mWebSocketClient.send(message);
        // Wait for 100 milliseconds
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
