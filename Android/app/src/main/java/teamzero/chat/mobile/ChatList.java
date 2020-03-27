package teamzero.chat.mobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.Handler;
import android.os.Bundle;
import android.os.AsyncTask;

import android.graphics.Color;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;

import java.util.ArrayList;
import java.util.List;

import database.AppDatabaseClient;
import database.StoredChatList;
import database.UsersOnDevice;

import tools.DHUtilities;
import tools.JSONConstructor;
import tools.RSAUtilities;


public class ChatList extends AppCompatActivity {

    ListView usersList;
    TextView noChatsFoundTextDisplay;
    FloatingActionButton newChatBtn;

    List<StoredChatList> storedChatList = new ArrayList<>();
    ProgressDialog pd;

    private Handler handlerForChats = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getStoredChatList();
            // Run a check every 1 second
            handlerForChats.postDelayed(this, 1000);
        }
    };

    public void startHandlerShowChats() {
        // Start the handler and run it every 1 second
        handlerForChats.postDelayed(runnable, 1000);
    }

    public void stopHandlerShowChats() {
        // Close the handler with the runnable action when you go to other activities
        handlerForChats.removeCallbacks(runnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_list_layout);

        usersList = (ListView) findViewById(R.id.usersList);
        noChatsFoundTextDisplay = (TextView) findViewById(R.id.noChatsFoundText);
        newChatBtn = (FloatingActionButton) findViewById(R.id.newChatButton);

        pd = new ProgressDialog(ChatList.this);
        pd.setMessage("Loading ...");
        pd.show();

        Snackbar.make(findViewById(R.id.usersList), "Welcome back " + UserDetails.username, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();

        //OLD --> Used directly to show the chats onCreate: getStoredChatList();
        startHandlerShowChats();

        // When the user clicks on a specific chat from his history, go to that conversation
        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                UserDetails.chatWith = storedChatList.get(position).getUsername();

                System.out.println(UserDetails.chatWith + " 's Public Key = " + storedChatList.get(position).getPublicKey());

                startActivity(new Intent(ChatList.this, Chat.class));
            }
        });

        // When a user clicks on the "new chat" button, go to the "new chat" page
        newChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatList.this, NewChat.class));
            }
        });
    }

    private void getStoredChatList() {

        System.out.println("Inside getStoredChatList");

        class GetStoredChatList extends AsyncTask<Void, Void, List<StoredChatList>> {

            @Override
            protected List<StoredChatList> doInBackground(Void... voids) {

                // If received a message from a user that does not belong to the chat list, insert it

                if(!UserDetails.messages.isEmpty()) {
                    for (String userThatSentNewMessage : UserDetails.messages.keySet()) {
                        List<StoredChatList> getUserFromChatList = AppDatabaseClient
                                .getInstance(getApplicationContext())
                                .getAppDatabase()
                                .storedChatListDao()
                                .getUserFromChatList(userThatSentNewMessage, UserDetails.username);

                        // If the user that sent the new message is not in the clients database, insert it
                        if(getUserFromChatList.isEmpty()) {

                            StoredChatList scl = new StoredChatList();
                            scl.setUsername(userThatSentNewMessage);
                            scl.setLastMessageContent("Last message here");
                            // Retrieve the public key of the sender using the GETPUBLICKEY JSON request
                            String publicKey = "BLANK";

                            try {
                                WebSocketHandler.getSocket().sendMessageAndWait(new JSONConstructor().constructGetPublicKeyJSON(userThatSentNewMessage));

                                // Get response from server and parse it
                                JSONObject responseJSON = new JSONObject(WebSocketHandler.getSocket().getResponse());
                                if (responseJSON.get("REPLY").toString().equalsIgnoreCase("GETPUBLICKEY: SUCCESS")) {
                                    if(responseJSON.has("publicKey"))
                                        publicKey = responseJSON.get("publicKey").toString();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            scl.setPublicKey(publicKey);


                            // TODO: Debugging purposes only -- Will keep what is inside 'if' statement and delete 'else'

                            if(!publicKey.equalsIgnoreCase("BLANK")) {
                                /* Shared secret key computation start */

                                //Step 1: compute DHPublicKey of other user

                                PublicKey publicKeyOfUser = DHUtilities.computeDHPublicKeyfromBase64String(publicKey);


                                //Step 2: Retrieve myPrivateKey & convert to DHPrivateKey
                                String myPrivateKeyStr = AppDatabaseClient.getInstance(getApplicationContext()).getAppDatabase().usersOnDeviceDao().getUserPrivateKey(UserDetails.username);
                                System.out.println("myPrivateKeyStr: " + myPrivateKeyStr);
                                PrivateKey myPrivateKey = DHUtilities.computeDHPrivateKeyfromBase64String(myPrivateKeyStr);

                                byte[] sharedKey = new byte[]{};
                                try {
                                    sharedKey = DHUtilities.recipientAgreementBasic(myPrivateKey, publicKeyOfUser);
                                } catch (GeneralSecurityException e) {
                                    System.out.println("Could not compute SharedKey");
                                    e.printStackTrace();
                                }

                                /*end shared secret key compute */

                                // TODO: This part directly takes the hardcoded DH key
                                String sharedPassphrase = DHUtilities.generateSharedKey(publicKey, myPrivateKeyStr);

                                System.out.println("sharedPassphrase = " + sharedPassphrase);

                                //test
                                System.out.println(new String(sharedKey));

                                // set the shared key in stored chat list details as a String
                                // TODO: Set to sharedKey instead (use next line to the following one)
                                scl.setSharedSecretKey(sharedPassphrase);
                                //scl.setSharedSecretKey(new String(sharedKey));
                            }
                            else scl.setSharedSecretKey("2646294A404E635266556A576E5A7234");

                            scl.setChatBelongsTo(UserDetails.username);

                            // Add the user into the local chat list database
                            AppDatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                                    .storedChatListDao()
                                    .insert(scl);
                        }
                    }
                }

                // Get all the chats for this user and use them to refresh the list of chats

                // SELECT * FROM storedchatlist WHERE chat_belongs_to LIKE UserDetails.username
                List<StoredChatList> scl = AppDatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .storedChatListDao()
                        .getChatsForClient(UserDetails.username);
                return scl;
            }

            @Override
            protected void onPostExecute(List<StoredChatList> scl) {
                super.onPostExecute(scl);
                // Get the chat list and store it on main thread
                storedChatList = scl;
                displayChatList();
            }
        }

        GetStoredChatList gt = new GetStoredChatList();
        gt.execute();
    }

    public void displayChatList() {

        if(storedChatList.size() == 0) {
            noChatsFoundTextDisplay.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        }

        else {
            noChatsFoundTextDisplay.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);

            // Overridden the getView of ArrayAdapter in order to access both text1 and text2 (from simple_list_item_2) and write on them right away
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, storedChatList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                    text1.setText(storedChatList.get(position).getUsername());
                    if(UserDetails.messages.containsKey(storedChatList.get(position).getUsername())) {
                        text2.setTextColor(Color.RED);
                        text2.setText("New messages found");
                    }
                    else {
                        text2.setTextColor(Color.BLACK);
                        text2.setText("No new messages");
                    }

                    //text2.setText(storedChatList.get(position).getLastMessageContent());

                    return view;
                }
            };
            usersList.setAdapter(adapter);
        }

        // Close the progress dialog when action is finished
        pd.dismiss();

    }

    // TODO: Make searching through chats functional
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        /*
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        */
        return true;
    }

    // Called when the user selects any item from the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Get the id of the selected menu item to determine what the user clicked
        switch (item.getItemId()) {

            case R.id.hide_chat_history:
                hideChatHistory();
                return true;

            case R.id.sign_out:
                signOutOrUnregister("Sign Out");
                return true;

            case R.id.unregister:
                signOutOrUnregister("Unregister");
                return true;

            case R.id.profile_picture:
                // TODO: Choose profile picture -- Goto profile page
                return true;

            case R.id.delete_all_chats:
                deleteAllChatsPrompt();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void hideChatHistory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("ON", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                UserDetails.historyIsHidden = false;
            }
        });

        builder.setNegativeButton("OFF", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                UserDetails.historyIsHidden = true;
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setTitle("Chat History");
        dialog.setMessage("Turn the chat history ON or OFF?");
        dialog.show();
    }

    public void signOutOrUnregister(final String choice) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton(choice, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Sign Out OR Unregister

                // Stops the handler that refreshes the chat list display
                stopHandlerShowChats();

                if(choice.equalsIgnoreCase("Sign Out")) {
                    // Do nothing and just close the socket
                }

                if(choice.equalsIgnoreCase("Unregister")) {

                    // Send unregister JSON request to server
                    try {
                        WebSocketHandler.getSocket().sendMessage(new JSONConstructor().constructUnregisterJSON(UserDetails.username, UserDetails.password));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Remove the user from the local database and delete all of its content, including chats
                    removeUserFromDatabase();

                }

                WebSocketHandler.getSocket().closeConnection();

                // Go back to home login/register screen
                startActivity(new Intent(ChatList.this, MainActivity.class));
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog, go back
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setTitle(choice);
        dialog.setMessage("Are you sure you want to " + choice.toLowerCase() + "?");
        dialog.show();
    }

    public void deleteAllChatsPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked DELETE button
                deleteAllChats();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog, go back
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setTitle("Delete all chats");
        dialog.setMessage("Are you sure you want to delete all the chats?");
        dialog.show();
    }

    private void deleteAllChats() {
        class DeleteAllChats extends AsyncTask<Void, Void, List<StoredChatList>> {

            @Override
            protected List<StoredChatList> doInBackground(Void... voids) {
                // DELETE FROM storedchatlist
                System.out.println("Deleting all chats (doInBackground)");
                AppDatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .storedChatListDao()
                        .deleteAllChatsForClient(UserDetails.username);
                return null;
            }

            @Override
            protected void onPostExecute(List<StoredChatList> scl) {
                super.onPostExecute(scl);
                // Refresh
                System.out.println("Deleting all chats (onPostExecute)");
            }
        }

        DeleteAllChats gt = new DeleteAllChats();
        gt.execute();
    }

    private void removeUserFromDatabase() {
        class RemoveUserFromDevice extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                System.out.println("Deleting user from device (doInBackground)");
                AppDatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .usersOnDeviceDao()
                        .deleteUserFromDevice(UserDetails.username);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                System.out.println("Deleting user from device (onPostExecute)");
            }
        }

        RemoveUserFromDevice RUFD = new RemoveUserFromDevice();
        RUFD.execute();
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
        Toast.makeText(getApplicationContext(), "For the privacy and security of your data, the back button has been disabled\n\n" +
                "If you wish to sign out, please click on the three dots (...) on the upper-right corner" +
                "and select 'Sign Out' ", Toast.LENGTH_LONG).show();
    }

}