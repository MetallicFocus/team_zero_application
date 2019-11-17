package teamzero.chat.mobile;

import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.view.View;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import database.AppDatabaseClient;
import database.StoredChatList;

public class NewChat extends AppCompatActivity {

    TextView suggestiveTextTextView;
    FloatingActionButton searchUsersBtn;
    EditText searchUsersEditText;

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

        searchUsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                suggestiveTextTextView.setVisibility(View.GONE);
                // TODO: Fetch list of users from server and display -- Try using ListView instead of RecyclerView
                // Start testing area -- Delete after testing ends
                addUserToStoredChatList();
                // End testing area
            }
        });
    }

    private void addUserToStoredChatList() {

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
