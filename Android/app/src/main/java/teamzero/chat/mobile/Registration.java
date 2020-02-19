package teamzero.chat.mobile;

import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.view.View;

import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.List;

import org.bouncycastle.util.encoders.Base64;

import database.AppDatabaseClient;
import database.StoredChatList;
import database.UsersOnDevice;
import database.UsersOnDeviceDao;
import tools.DHUtilities;
import tools.JSONConstructor;
import tools.RSAUtilities;
import tools.RegistrationValidator;

public class Registration extends AppCompatActivity {

    RegistrationValidator rV = new RegistrationValidator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);

        // Check if the SupportActionBar is instantiated
        assert getSupportActionBar() != null;
        // If it is, display a back button on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

            KeyPair publicPrivateKeys = DHUtilities.generateKeyPair();
            //KeyPair publicPrivateKeys = RSAUtilities.generateKeyPair();

            // TODO: Cleanup
            System.out.println("-- PRE-ENCODING --");
            System.out.println("public key = " + publicPrivateKeys.getPublic());
            System.out.println(publicPrivateKeys.getPublic().getEncoded());
            System.out.println(Base64.toBase64String(publicPrivateKeys.getPublic().getEncoded()));

            System.out.println("private key = " + publicPrivateKeys.getPrivate());
            System.out.println(publicPrivateKeys.getPrivate().getEncoded());
            System.out.println(Base64.toBase64String(publicPrivateKeys.getPrivate().getEncoded()));

            // Send register JSON request to server
            WebSocketHandler.getSocket().sendMessageAndWait(new JSONConstructor().constructRegisterJSON(username, password, email, picture, Base64.toBase64String(publicPrivateKeys.getPublic().getEncoded())));

            //Thread.sleep(500);

            // Get response from server and parse it. If registration is successful, try to login
            JSONObject responseJSON = new JSONObject(WebSocketHandler.getSocket().getResponse());

            if (responseJSON.get("REPLY").toString().equalsIgnoreCase("REGISTER: SUCCESS")) {

                // Store the user into device's local database

                UsersOnDevice UOD = new UsersOnDevice();
                UOD.setUsername(username);
                UOD.setPrivateKey(Base64.toBase64String(publicPrivateKeys.getPrivate().getEncoded()));

                System.out.println("UOD.getUsername = " + UOD.getUsername());
                System.out.println("UOD.getPrivateKey = " + UOD.getPrivateKey());

                storeNewClientOnDevice(UOD);

                // Then LOGIN
                WebSocketHandler.getSocket().sendMessageAndWait(new JSONConstructor().constructLoginJSON(username, password));

                //Thread.sleep(500);

                responseJSON = new JSONObject(WebSocketHandler.getSocket().getResponse());

                if(responseJSON.get("REPLY").toString().equalsIgnoreCase("LOGIN: SUCCESS")) {

                    // Assign username and password to UserDetails only after a success response from server
                    UserDetails.username = username;
                    UserDetails.password = password;

                    startActivity(new Intent(Registration.this, ChatList.class));
                }
                else Toast.makeText(getApplicationContext(), R.string.registration_success_login_fail_text, Toast.LENGTH_LONG).show();

            }
            else Toast.makeText(getApplicationContext(), R.string.registration_unsuccessful_text, Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void storeNewClientOnDevice(final UsersOnDevice UOD) {
        class StoreNewClientOnDevice extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                AppDatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .usersOnDeviceDao()
                        .insert(UOD);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }

        StoreNewClientOnDevice store = new StoreNewClientOnDevice();
        store.execute();
    }

    // Finishes current activity (dismisses dialogs, closes search) and goes to the parent activity
    // The method is called when the user clicks on the back button on the upper-left hand side
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
