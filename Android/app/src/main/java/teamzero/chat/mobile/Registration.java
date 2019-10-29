package teamzero.chat.mobile;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.PatternSyntaxException;

public class Registration extends AppCompatActivity {

    /* TODO:
        1) Methods to check if username is valid and available
        2) Method that checks if both e-mails are the same AND in a correct format
        3) Method that checks if both passwords are the same AND strong enough
        4) Protect against SQL Injection
        5) Method that passes the information to the server to store in database
            5') Give the user a message stating that he should check his e-mail to confirm the account
                and go back to the login screen

     */

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

        if(validateUsername(username)) {
            // Check if username is available to register
            //checkUsernameAvailability(username);

            // Check if passwords are valid and if both are the same
            //validatePasswords(password, confirmPassword);

            // Check if e-mails are valid and if both are the same
            //validateEmails(email, confirmEmail);
        }
        else Toast.makeText(getApplicationContext(), "Username is invalid!\nPlease try another username!", Toast.LENGTH_SHORT).show();
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

            if (!valid)
                return false;

        } catch (PatternSyntaxException ex) {
            // Invalid regex
            ex.printStackTrace();
        }
        return true;
    }

    public void checkUsernameAvailability(String username) {
        /* Check database for the availability of chosen username */
    }

    public void validatePasswords(String password, String confirmPassword) {
        /* Check if:
            1) Password contains at least 1 uppercase character
            2) Password contains at least 1 number
            3) Password contains at least 1 special character
            4) Password is between 8 and 12 characters
            5) They are the same
         */

        // We check 1-4 only for one password (no need for both)

        // Check if passwords are the same

    }

    public void validateEmails(String email, String confirmEmail) {
        /* Check if:
            1) E-mail contains '@' character
            2) E-mail contains at least a dot ('.')
           Make this method RFC822 compliant?
           http://www.ex-parrot.com/~pdw/Mail-RFC822-Address/Mail-RFC822-Address.html
         */

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

    public void sendDataToDB(String username, String password, String email) {
        /* Method that delivers information to store in database
        *
        * To Think: Use PreparedStatement to make SQL Injection impossible by placing the data
        * directly inside the database (not affecting the INSERT statement in any way)
        *
        * */
    }

}
