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

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import database.AppDatabaseClient;
import database.StoredChatList;

import tools.JSONConstructor;


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

                // TODO: Start Activity --> Goto chat page with X person
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
                // SELECT * FROM storedchatlist
                List<StoredChatList> scl = AppDatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .storedChatListDao()
                        .getAll();
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

    public void signOutOrUnregister(final String choice) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton(choice, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Sign Out OR Unregister

                if(choice.equalsIgnoreCase("Sign Out")) {
                    // Do nothing and just close the socket
                }

                if(choice.equalsIgnoreCase("Unregister")) {

                    // Send unregister JSON request to server
                    try {
                        WebSocketHandler.getSocket().sendMessage(new JSONConstructor().constructUnregisterJSON(UserDetails.username, UserDetails.password));

                        // TODO: Deal with the case when unregistering is unsuccessful

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                WebSocketHandler.getSocket().closeConnection();

                // Stops the handler that refreshes the chat list display when the user signs out
                stopHandlerShowChats();

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
                AppDatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .storedChatListDao()
                        .deleteAll();
                return null;
            }

            @Override
            protected void onPostExecute(List<StoredChatList> scl) {
                super.onPostExecute(scl);
                // Refresh
                startActivity(new Intent(ChatList.this, ChatList.class));
            }
        }

        DeleteAllChats gt = new DeleteAllChats();
        gt.execute();
    }

}