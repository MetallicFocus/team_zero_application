package teamzero.chat.mobile;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
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
    private Boolean receivedResponse = false;

    public WebSocket() {
        connectWebSocket();
    }

    private void connectWebSocket() {

        URI uri;
        try {
            // Used the following URI for testing purposes only
            // TODO: Change URI to Heroku server
            uri = new URI("ws://10.40.177.152:1234");
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
                receivedResponse = true;

                try {
                    JSONObject responseJSON = new JSONObject(message);

                    // When the user receives a message from another user
                    if(responseJSON.get("type").toString().equalsIgnoreCase("TEXT")) {
                        // TODO: Add message to local database to retrieve the latest N messages from chat X
                        UserDetails.messageContent = responseJSON.get("message").toString();
                        UserDetails.messageFrom = responseJSON.get("sender").toString();

                        if(!UserDetails.messages.containsKey(UserDetails.messageFrom)) {
                            ArrayList<String> x = new ArrayList<>();
                            x.add(UserDetails.messageContent);
                            UserDetails.messages.put(UserDetails.messageFrom, x);
                        }
                        else {
                            ArrayList<String> x = UserDetails.messages.get(UserDetails.messageFrom);
                            x.add(UserDetails.messageContent);
                            UserDetails.messages.put(UserDetails.messageFrom, x);
                        }

                        System.out.println(UserDetails.messageFrom + ": "+ UserDetails.messageContent);

                        receivedResponse = false;
                    }
                    if(responseJSON.get("type").toString().equalsIgnoreCase("REPLY")) {
                        if(responseJSON.get("REPLY").toString().startsWith("TEXT")) {
                            // TODO: If the user is offline, show the message with 1 check sign, else with 2 check signs
                            // View status of the sent message of the client
                            System.out.println(responseJSON.get("message").toString());
                            receivedResponse = false;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    // Debugging purposes only
    public Boolean getReceivedResponse() { return receivedResponse; }

    public Boolean waitForResponse() {
        if(receivedResponse) {
            receivedResponse = false;
            return true;
        }
        return false;
    }

    public void closeConnection() {
        if(!mWebSocketClient.getConnection().isClosed())
            mWebSocketClient.close();
    }

    // There are 2 different sendMessage methods -
    // 1) Send message to server with waiting period for a response
    // 2) Send message to other user without any waiting
    public void sendMessageAndWait(String message, final Boolean isLogin) {
        if(mWebSocketClient.getConnection().isOpen())
            mWebSocketClient.send(message);

        System.out.println("BEFORE=" + getReceivedResponse());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {

                while(!WebSocketHandler.getSocket().waitForResponse()) {
                    // Wait for 1st response OR timeout to pass
                }
                if(isLogin)
                    while(!WebSocketHandler.getSocket().waitForResponse()) {
                        // Wait for 2nd response OR timeout to pass
                    }
                return "Ready!";
            }
        });

        try {
            // Timeout of 10 seconds to try the action
            System.out.println(future.get(10, TimeUnit.SECONDS));
        } catch (TimeoutException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            future.cancel(true);
            System.out.println("Timeout!");
        }

        System.out.println("AFTER=" + getReceivedResponse());

        executor.shutdown();
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
