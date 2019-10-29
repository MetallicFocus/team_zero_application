package teamzero.chat.mobile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class Registration extends AppCompatActivity {

    /* TODO:
        1) Method that checks if both e-mails are the same AND in a correct format
        2) Method that checks if both passwords are the same AND strong enough
        3) Method that passes the information to the server to store in database
            3') Give the user a message stating that he should check his e-mail to confirm the account
                and go back to the login screen
        4) Protect against SQL Injection
        5) Method to check if username is available
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

        // Check if username is valid and filter it for SQL injection
        validateUsername(username);

        // Check if username is available to register (if and only if is valid first)
        checkUsernameAvailability(username);

        // Check if passwords are valid, filter them for SQL injection and check if they are the same
        validatePasswords(password, confirmPassword);

        // Check if e-mails are valid, filter them for SQL injection and check if they are the same
        validateEmails(email, confirmEmail);
    }

    public void validateUsername(String username) {
        /* Check if:
            1) It does not contain illegal characters
            2) It does not begin with numbers
         */
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
         */

    }

    public void validateEmails(String email, String confirmEmail) {
        /* Check if:
            1) E-mail contains '@' character
            2) E-mail contains at least a dot ('.')
         */

    }

    public void filterForSQLInjection(String text) {
        /* Method that checks for SQL injection in text */

    }

}
