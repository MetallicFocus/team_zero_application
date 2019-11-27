package teamzero.chat.mobile;

import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.view.View;

import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import database.AppDatabaseClient;
import database.StoredChatList;
import tools.JSONConstructor;

public class NewChat extends AppCompatActivity {

    TextView suggestiveTextTextView;
    FloatingActionButton searchUsersBtn;
    EditText searchUsersEditText;
    ListView usersList;
    List<String> usersFoundList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_chat_layout);

        // Check if the SupportActionBar is instantiated
        assert getSupportActionBar() != null;
        // If it is, display a back button on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        suggestiveTextTextView = (TextView) findViewById(R.id.searchUsersSuggestiveTextView);
        searchUsersBtn = (FloatingActionButton) findViewById(R.id.SearchUsersButton);
        searchUsersEditText = (EditText) findViewById(R.id.SearchUsersEditText);
        //usersList = (ListView) findViewById(R.id.displayUsersFoundList);

        searchUsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchForUser = searchUsersEditText.getText().toString();

                // If the user clicks on the search button without text input, send GETALLCONTACTS request JSON
                if(searchForUser.isEmpty()) {

                    try {
                        System.out.println(new JSONConstructor().constructGetAllContactsJSON());
                        WebSocketHandler.getSocket().sendMessage(new JSONConstructor().constructGetAllContactsJSON());

                        // TODO: Use ExecutorService to wait until response is received or timeout
                        Thread.sleep(500);

                        // Get response from server and parse it
                        JSONObject responseJSON = new JSONObject(WebSocketHandler.getSocket().getResponse());

                        if (responseJSON.get("REPLY").toString().equalsIgnoreCase("GETALLCONTACTS: SUCCESS")) {

                            if(responseJSON.has("contacts")) {
                                addUsersFoundToList(responseJSON);
                            }
                            else Toast.makeText(getApplicationContext(), R.string.search_all_contacts_not_found_text, Toast.LENGTH_LONG).show();

                        }
                        else Toast.makeText(getApplicationContext(), R.string.fetching_from_server_error_text, Toast.LENGTH_SHORT).show();

                    } catch (JSONException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    // If the user gives text input, send SEARCHCONTACTS request JSON
                    try {
                        System.out.println(new JSONConstructor().constructSearchContactsJSON(searchForUser));
                        WebSocketHandler.getSocket().sendMessage(new JSONConstructor().constructSearchContactsJSON(searchForUser));

                        // TODO: Use ExecutorService to wait until response is received or timeout
                        Thread.sleep(500);

                        // Get response from server and parse it
                        JSONObject responseJSON = new JSONObject(WebSocketHandler.getSocket().getResponse());
                        if (responseJSON.get("REPLY").toString().equalsIgnoreCase("SEARCHCONTACTS: SUCCESS")) {

                            if(responseJSON.has("contacts")) {
                                addUsersFoundToList(responseJSON);
                            }
                            else Toast.makeText(getApplicationContext(), R.string.search_contact_not_found_text, Toast.LENGTH_LONG).show();

                        }
                        else Toast.makeText(getApplicationContext(), R.string.fetching_from_server_error_text, Toast.LENGTH_SHORT).show();

                    } catch (JSONException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // TODO: Fetch list of users from server and display -- Try using ListView instead of RecyclerView
                // Start testing area -- Delete after testing ends
                //addUserToStoredChatList();
                // End testing area
            }
        });
    }

    private void addUsersFoundToList(JSONObject contactList) throws JSONException {

        // TODO: Output the list of contacts found on screen
        //JSONObject contacts = new JSONObject(contactList);
        System.out.println(contactList.get("contacts"));
        /*JSONArray ja = contactList.getJSONArray("contacts");
        for (int i = 0; i < ja.length(); i++) {
            usersFoundList.add("user");
            usersFoundList.add("offline");
        }
        displayChatList(); */
    }

    public void displayChatList() {

        suggestiveTextTextView.setVisibility(View.GONE);
        usersList.setVisibility(View.VISIBLE);

        // Overridden the getView of ArrayAdapter in order to access both text1 and text2 (from simple_list_item_2) and write on them right away
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, usersFoundList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText("test1");
                text2.setText("test2");
                return view;
            }
        };
        usersList.setAdapter(adapter);

    }

    private void addUserToStoredChatList() {

        // TODO: Add the user only if it does not exist already in local database

        final String searchUsersEditTextString = searchUsersEditText.getText().toString();

        class AddUserToStoredChatList extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                StoredChatList scl = new StoredChatList();
                scl.setUsername(searchUsersEditTextString);
                scl.setLastMessageContent("Last message here");
                scl.setLastMessageDate(null);

                // Add the user into the local chat list database
                AppDatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .storedChatListDao()
                        .insert(scl);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                finish();
                startActivity(new Intent(getApplicationContext(), ChatList.class));
                Toast.makeText(getApplicationContext(), "Chat created", Toast.LENGTH_LONG).show();
            }
        }

        AddUserToStoredChatList st = new AddUserToStoredChatList();
        st.execute();
    }

    // Finishes current activity (dismisses dialogs, closes search) and goes to the parent activity
    // The method is called when the user clicks on the back button on the upper-left hand side
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
