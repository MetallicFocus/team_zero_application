package teamzero.chat.mobile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Registration extends AppCompatActivity {

    /* TODO:
        1) Method that checks if both e-mails are the same AND in a correct format
        2) Method that checks if both passwords are the same AND strong enough
        3) Method that passes the information to the server to store in database
            3') Give the user a message stating that he should check his e-mail to confirm the account
                and go back to the login screen
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);
    }

}
