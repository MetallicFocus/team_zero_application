package teamzero.chat.mobile;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {

                while(!mWebSocketClient.getConnection().isOpen()) {
                    // Wait for connection to open OR timeout to pass
                }
                return "Ready!";
            }
        });

        try {
            // Timeout of 10 seconds to try the connection
            System.out.println(future.get(10, TimeUnit.SECONDS));
        } catch (TimeoutException | ExecutionException | InterruptedException e) {
            future.cancel(true);
            System.out.println("Timeout!");
        }

        executor.shutdownNow();
    }

    public String getResponse() {
        return response;
    }

    public void closeConnection() {
        if(!mWebSocketClient.getConnection().isClosed())
            mWebSocketClient.close();
    }

    public void sendMessage(String message) {
        if(mWebSocketClient.getConnection().isOpen())
            mWebSocketClient.send(message);
        // Busy waiting to get message because ws is async (cannot make sync method).
        // TODO: Think of better approach
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
