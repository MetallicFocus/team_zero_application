package teamzero.chat.mobile;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    Button loginButton;
    Button registerButton;
    EditText usernameInput;
    WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the WebSocket (i.e. connection is established)
        webSocket = new WebSocket();

        // Set the socket inside the handler and use it from now on
        WebSocketHandler.setSocket(webSocket);

        // Testing WebSocket handler from this activity
        WebSocketHandler.getSocket().sendMessage("Test Message 1");
        System.out.println("Response test from Main activity: " + WebSocketHandler.getSocket().getResponse());
        WebSocketHandler.getSocket().sendMessage("Test Message 2");
        System.out.println("Response test from Main activity: " + WebSocketHandler.getSocket().getResponse());
        // End testing websocket handler

        loginButton = (Button) findViewById(R.id.login);
        registerButton = (Button) findViewById(R.id.register);
        usernameInput = (EditText) findViewById(R.id.editTextUsername);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Registration.class));
            }
        });

        // TODO: Contact the server to establish connection and login after clicking on login btn
        // Start of testing zone:
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserDetails.username = usernameInput.getText().toString();

                startActivity(new Intent(MainActivity.this, ChatList.class));
            }
        });
        // End of testing zone.
    }

}
