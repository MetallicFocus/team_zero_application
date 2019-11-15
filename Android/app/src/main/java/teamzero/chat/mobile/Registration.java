package teamzero.chat.mobile;

import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.view.View;

import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.util.regex.PatternSyntaxException;

import tools.JSONConstructor;

public class Registration extends AppCompatActivity {

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

        if(validateUsername(username) &&
                validatePasswords(password, confirmPassword) &&     // Check if passwords are valid and if both are the same
                validateEmails(email, confirmEmail)) {              // Check if e-mails are valid and if both are the same

            sendRegistrationRequestToServer(username, password, email, "null");
        }
    }

    public boolean validateUsername(String username) {
        /* Check if:
            1) The field is not empty
            2) It does not contain illegal characters (anything except '-', '.' and '_')
            3) It does not start with numbers
            4) It is between 3 and 30 characters
         */

        try {
            boolean valid = (username != null) && username.matches("\\b[a-zA-Z][a-zA-Z0-9\\-._]{3,30}\\b");

            if (!valid) {
                Toast.makeText(getApplicationContext(), R.string.invalid_username_text, Toast.LENGTH_LONG).show();
                return false;
            }

        } catch (PatternSyntaxException ex) {
            // Invalid regex
            ex.printStackTrace();
        }
        return true;
    }

    public boolean validatePasswords(String password, String confirmPassword) {
        /* Check if:
            1) Password contains at least 1 uppercase character     (?=.*[A-Z])
            2) Password contains at least 1 lowercase character     (?=.*[a-z])
            3) Password contains at least 1 digit                   (?=.*[0-9])
            4) Password contains at least 1 special character       (?=.*[@#!?$%^&+])
            5) Password must not contain white spaces               (?=\S+$)
            6) Password is between 6 and 30 characters              {6,30}
         */

        // We check 1-6 only for one password (no need for both)

        try {
            boolean valid = (password != null) && password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#!?$%^&+])(?=\\S+$).{6,30}");

            if (!valid) {
                Toast.makeText(getApplicationContext(), R.string.invalid_password_text, Toast.LENGTH_LONG).show();
                return false;
            }

        } catch (PatternSyntaxException ex) {
            // Invalid regex
            ex.printStackTrace();
            return false;
        }

        // Check if passwords are the same
        try {
            if(!password.equals(confirmPassword)) {
                Toast.makeText(getApplicationContext(), R.string.invalid_confirmed_password_text, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch(NullPointerException ex1) {
            ex1.printStackTrace();
            return false;
        }
        return true;
    }

    // Make this method RFC822 compliant? (http://www.ex-parrot.com/~pdw/Mail-RFC822-Address/Mail-RFC822-Address.html)
    public boolean validateEmails(String email, String confirmEmail) {
        /* Check if:
            1) E-mail contains only one '@' character
            2) E-mail contains at least a dot ('.')
            3) E-mail contains between 2 and 6 characters after a dot ('.')
         */

        try {
            boolean valid = (email != null) && email.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}");

            if (!valid) {
                Toast.makeText(getApplicationContext(), R.string.invalid_email_text, Toast.LENGTH_SHORT).show();
                return false;
            }

        } catch (PatternSyntaxException ex) {
            // Invalid regex
            ex.printStackTrace();
            return false;
        }

        // Check if emails are the same
        try {
            if(!email.equals(confirmEmail)) {
                Toast.makeText(getApplicationContext(), R.string.invalid_confirmed_email_text, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch(NullPointerException ex1) {
            ex1.printStackTrace();
            return false;
        }
        return true;
    }

    public String filterForSQLInjection(String text) {
        /* Method that filters for SQL injection in text by escaping characters */
        String newText = null;
        if (text != null && text.length() > 0) {
            text = text.replace("\\", "\\\\");
            text = text.replace("'", "\\'");
            text = text.replace("\0", "\\0");
            text = text.replace("\n", "\\n");
            text = text.replace("\r", "\\r");
            text = text.replace("\"", "\\\"");
            text = text.replace("\\x1a", "\\Z");
            newText = text;
        }
        return newText;
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
