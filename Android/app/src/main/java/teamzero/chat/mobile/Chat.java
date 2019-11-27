package teamzero.chat.mobile;

import android.graphics.Color;
import android.os.Bundle;

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
                    // TODO: Change second argument into UserDetails.chatWith
                    WebSocketHandler.getSocket().sendMessage(new JSONConstructor().constructTextJSON(UserDetails.username, UserDetails.username, messageToSend.getText().toString()));
                    addMessageBox(messageToSend.getText().toString(), 1);

                    // TODO: Make use of ExecutorService
                    Thread.sleep(2000);

                    // TODO: Get responses and show them async
                    JSONObject responseJSON = new JSONObject(WebSocketHandler.getSocket().getResponse());
                    System.out.println(UserDetails.username + ": "+ responseJSON.get("message").toString());
                    addMessageBox(responseJSON.get("message").toString(), 2);
                } catch (JSONException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

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
        finish();
        return true;
    }

}
