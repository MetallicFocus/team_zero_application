package teamzero.chat.mobile;

import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import tools.JSONConstructor;

public class Chat extends AppCompatActivity {

    LinearLayout linearLayoutScrollView;
    ScrollView scrollView;
    EditText messageToSend;
    Button sendMsgButton;

    // Use the handler with a runnable in order to recognize fetched messages from the onMessage event of the WS
    // Once the messages are recognized, display them onto the chat screen using addMessageBox
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getMsg();
            // Run a check every 1 second
            handler.postDelayed(this, 1000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        setTitle(UserDetails.chatWith);

        linearLayoutScrollView = findViewById(R.id.linearLayoutSV);
        scrollView = findViewById(R.id.scrollView);
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

                System.out.println("You: " + messageToSend.getText().toString());

                try {
                    // Send TEXT JSON request to server to send to the other user
                    WebSocketHandler.getSocket().sendMessage(new JSONConstructor().constructTextJSON(UserDetails.username, UserDetails.chatWith, messageToSend.getText().toString()));
                    addMessageBox(messageToSend.getText().toString(), 1);

                    //Thread.sleep(2000);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // Start the handler and run it every 1 second
        handler.postDelayed(runnable, 1000);
    }

    public void getMsg() {
        System.out.println("Inside getMsg");
        // If there are new messages for this chat, display them

        // TODO: Gather the last N messages from the local database for this chat

        if(UserDetails.messages.containsKey(UserDetails.chatWith)) {
            System.out.println(UserDetails.messages);
            for(int i = 0; i < UserDetails.messages.get(UserDetails.chatWith).size(); i++) {
                addMessageBox(UserDetails.messages.get(UserDetails.chatWith).get(i), 2);
            }
            UserDetails.messages.remove(UserDetails.chatWith);
        }
    }

    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(Chat.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 17.0f;
        lp2.bottomMargin = 10;
        textView.setTextColor(Color.parseColor("black"));

        if(type == 1) {
            lp2.gravity = Gravity.LEFT;
            lp2.leftMargin = 20;
            lp2.topMargin = 4;
            lp2.rightMargin = 10;
            textView.setBackgroundColor(Color.parseColor("#06BD75"));
            //textView.setBackgroundResource(R.drawable.outgoing_message);
        }
        else {
            lp2.gravity = Gravity.RIGHT;
            lp2.rightMargin = 10;
            textView.setBackgroundColor(Color.parseColor("#33C4FF"));
            //textView.setBackgroundResource(R.drawable.incoming_message);
        }
        textView.setLayoutParams(lp2);
        linearLayoutScrollView.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    // Finishes current activity (dismisses dialogs, closes search, etc.) and goes to the parent activity
    // The method is called when the user clicks on the back button on the upper-left hand side
    @Override
    public boolean onSupportNavigateUp() {
        // Close the handler with the runnable action when you go to other activities
        handler.removeCallbacks(runnable);
        finish();
        return true;
    }

}
