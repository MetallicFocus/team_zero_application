package teamzero.chat.mobile;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;

public class Chat extends AppCompatActivity {

    EditText messageToSend;
    Button sendMsgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        setTitle(UserDetails.chatWith);

        messageToSend = (EditText) findViewById(R.id.MsgBox);
        sendMsgButton = (Button) findViewById(R.id.SendMsg);

        // Check if the SupportActionBar is instantiated
        assert getSupportActionBar() != null;
        // If it is, display a back button on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // When a user clicks on the "send" button in order to send his message
        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Message to send towards " + UserDetails.chatWith + " = " + messageToSend.getText().toString());
                // TODO: Send request JSON to server with message towards recipient
            }
        });

        // TODO: Display messages from other user in RecyclerView

    }

    // Finishes current activity (dismisses dialogs, closes search, etc.) and goes to the parent activity
    // The method is called when the user clicks on the back button on the upper-left hand side
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
