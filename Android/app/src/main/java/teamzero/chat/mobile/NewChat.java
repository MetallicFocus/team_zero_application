package teamzero.chat.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class NewChat extends AppCompatActivity {

    TextView suggestiveTextTextView;
    FloatingActionButton searchUsersBtn;
    EditText searchUsersEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_chat_layout);

        suggestiveTextTextView = (TextView) findViewById(R.id.searchUsersSuggestiveTextView);
        searchUsersBtn = (FloatingActionButton) findViewById(R.id.SearchUsersButton);
        searchUsersEditText = (EditText) findViewById(R.id.SearchUsersEditText);

        Snackbar.make(findViewById(R.id.recyclerViewUsersList), "Currently Under Development . . . New Chat Feature", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();

        // TODO: Search for users in database, display user inside RecyclerView and let the user start a chat
        // TODO IDEA: Use ListView instead of RecyclerView
        searchUsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                suggestiveTextTextView.setVisibility(View.GONE);
            }
        });
    }

}
