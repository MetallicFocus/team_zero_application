package teamzero.chat.mobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.PatternSyntaxException;

public class Registration extends AppCompatActivity {

    /* TODO:
        1) Methods to check if username is:
         1') valid [X]
         2') available  []
        2) Method that checks if both e-mails are the same AND in a correct format  [X]
        3) Method that checks if both passwords are the same AND strong enough      [X]
        4) Protect against SQL Injection                                            [X]
        5) Method that passes the information to the server to store in database    []

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);

        // Testing WebSocket handler from this activity
        System.out.println("Response test from Registration activity: " + WebSocketHandler.getSocket().getResponse());
        WebSocketHandler.getSocket().sendMessage("Test Message 3");
        System.out.println("Response test from Registration activity: " + WebSocketHandler.getSocket().getResponse());
        WebSocketHandler.getSocket().closeConnection();
        // End of testing
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
                checkUsernameAvailability(username) &&              // Check if username is available to register
                validatePasswords(password, confirmPassword) &&     // Check if passwords are valid and if both are the same
                validateEmails(email, confirmEmail)) {              // Check if e-mails are valid and if both are the same

            sendDataToServer(username, password, email);
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
                Toast.makeText(getApplicationContext(), "Username is invalid!\nPlease try another username!", Toast.LENGTH_SHORT).show();
                return false;
            }

        } catch (PatternSyntaxException ex) {
            // Invalid regex
            ex.printStackTrace();
        }
        return true;
    }

    // TODO: Method that checks availability of username from database (fetch information)
    public boolean checkUsernameAvailability(String username) {
        /* Check database for the availability of chosen username */

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
                Toast.makeText(getApplicationContext(), "Invalid password!\n" +
                        "Please make sure:\n\n" +
                        "1) Password contains at least 1 uppercase character\n" +
                        "2) Password contains at least 1 lowercase character\n" +
                        "3) Password contains at least 1 digit\n" +
                        "4) Password contains at least 1 special character\n" +
                        "5) Password is between 6 and 30 characters", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "Confirmed password is not identical!\nPlease try again!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch(NullPointerException ex1) {
            ex1.printStackTrace();
            return false;
        }
        return true;
    }

    // TODO: Make this method RFC822 compliant? (http://www.ex-parrot.com/~pdw/Mail-RFC822-Address/Mail-RFC822-Address.html)
    public boolean validateEmails(String email, String confirmEmail) {
        /* Check if:
            1) E-mail contains only one '@' character
            2) E-mail contains at least a dot ('.')
            3) E-mail contains between 2 and 6 characters after a dot ('.')
         */

        try {
            boolean valid = (email != null) && email.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}");

            if (!valid) {
                Toast.makeText(getApplicationContext(), "Invalid email format!\n" +
                        "Please type in your email again", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "Confirmed email is not identical!\nPlease type in your email again!", Toast.LENGTH_SHORT).show();
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

    public void sendDataToServer(String username, String password, String email) {
        /* Method that delivers information to store in database
        *
        * To Think: Use PreparedStatement to make SQL Injection impossible by placing the data
        * directly inside the database (not affecting the INSERT statement in any way)
        *
        * */

        // TODO: Send the information in JSON format to server and get into account
        // Just for demo to go to the chat list screen:

        startActivity(new Intent(Registration.this, ChatList.class));
        // End of demo area
    }

}
