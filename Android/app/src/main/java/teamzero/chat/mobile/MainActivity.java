package teamzero.chat.mobile;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import tools.JSONConstructor;
import tools.RegistrationValidator;

public class MainActivity extends AppCompatActivity {

    Button loginButton;
    Button registerButton;
    EditText usernameInput;
    EditText passwordInput;
    String username;
    String password;
    WebSocket webSocket;

    RegistrationValidator rV = new RegistrationValidator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the WebSocket (i.e. connection is established)
        webSocket = new WebSocket();

        // Set the socket inside the handler and use it from now on
        WebSocketHandler.setSocket(webSocket);

        loginButton = (Button) findViewById(R.id.login);
        registerButton = (Button) findViewById(R.id.register);
        usernameInput = (EditText) findViewById(R.id.editTextUsername);
        passwordInput = (EditText) findViewById(R.id.editTextPwd);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Registration.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = usernameInput.getText().toString();
                password = passwordInput.getText().toString();

                // Do the below actions only if fields are secure from SQL Injection
                if(rV.validateUsername(username) && rV.validatePassword(password)) {

                    try {

                        // Send login JSON request to server
                        WebSocketHandler.getSocket().sendMessageAndWait(new JSONConstructor().constructLoginJSON(username, password));

                        //Thread.sleep(2000);

                        JSONObject responseJSON = new JSONObject(WebSocketHandler.getSocket().getResponse());

                        if (responseJSON.get("REPLY").toString().equalsIgnoreCase("LOGIN: SUCCESS")) {

                            // Assign username and password to UserDetails only after a success response from server
                            UserDetails.username = username;
                            UserDetails.password = password;

                            UserDetails.historyIsHidden = false;

                            startActivity(new Intent(MainActivity.this, ChatList.class));
                        }
                        else Toast.makeText(getApplicationContext(), R.string.login_failed_text, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else Toast.makeText(getApplicationContext(), R.string.login_failed_text, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        /*
         *  Disable the back button in the current screen by not calling super
         *
         *  This is important in order to not allow the user to see sensitive information
         *  from other users that we're previously logged into this device
         */
        Toast.makeText(getApplicationContext(), "For the privacy and security of your data, the back button has been disabled", Toast.LENGTH_SHORT).show();
    }

}
