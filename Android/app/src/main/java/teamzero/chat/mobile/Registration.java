package teamzero.chat.mobile;

import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.view.View;

import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import tools.JSONConstructor;
import tools.RegistrationValidator;

public class Registration extends AppCompatActivity {

    RegistrationValidator rV = new RegistrationValidator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);
    }

    public void checkFields(View view) {
        EditText usernameInput = (EditText) findViewById(R.id.editTextUsername);
        EditText passwordInput = (EditText) findViewById(R.id.editTextPwd);
        EditText confirmPasswordInput = (EditText) findViewById(R.id.editTextConfirmPwd);
        EditText emailInput = (EditText) findViewById(R.id.editTextEmail);
        EditText confirmEmailInput = (EditText) findViewById(R.id.editTextConfirmEmail);

        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        String email = emailInput.getText().toString();
        String confirmEmail = confirmEmailInput.getText().toString();

        System.out.println("username = " + username + "\n pwd = " + password + "\n conPwd = " + confirmPassword);
        System.out.println("email = " + email + "\n confirmEmail = " + confirmEmail);

        if(rV.validateUsername(username))
            if(rV.validatePassword(password))
                if(rV.passwordsAreEqual(password, confirmPassword))
                    if(rV.validateEmail(email))
                        if(rV.emailsAreEqual(email, confirmEmail))
                            sendRegistrationRequestToServer(username, password, email, "null");
                        else
                            Toast.makeText(getApplicationContext(), R.string.invalid_confirmed_email_text, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), R.string.invalid_email_text, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), R.string.invalid_confirmed_password_text, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), R.string.invalid_password_text, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(), R.string.invalid_username_text, Toast.LENGTH_LONG).show();
    }

    public void sendRegistrationRequestToServer(String username, String password, String email, Object picture) {

        try {
            System.out.println("JSON = " + new JSONConstructor().constructRegisterJSON(username, password, email, picture));

            // Send JSON to server
            WebSocketHandler.getSocket().sendMessage(new JSONConstructor().constructRegisterJSON(username, password, email, picture));

            // TODO: Get response from server and parse it. If SUCCESS, login. Else, refresh this screen
            //if(parseResponse(WebSocketHandler.getSocket().getResponse()).equalsIgnoreCase("success")) {
            startActivity(new Intent(Registration.this, ChatList.class));
            // }
            // else
            // Get error, display it in a toast and refresh this screen

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
