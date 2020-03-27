package teamzero.chat.mobile;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import android.util.DisplayMetrics;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import database.AppDatabaseClient;
import tools.AES256Cryptor;
import tools.AESUtilities;
import tools.JSONConstructor;

public class Chat extends AppCompatActivity {

    LinearLayout linearLayoutScrollView;
    ScrollView scrollView;
    EditText messageToSend;
    Button sendMsgButton;
    String sharedSecret = "";

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

        getSharedSecretKey();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("SHARED SECRET = " + sharedSecret);

        if(!UserDetails.historyIsHidden)
            showHistoryOfMessages(UserDetails.username, UserDetails.chatWith, "1");

        // When a user clicks on the "send" button in order to send his message
        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("You: " + messageToSend.getText().toString());

                try {
                    // Send TEXT JSON request to server to send to the other user

                    // encrypt message sent to other user
                    String unencryptedText = messageToSend.getText().toString();
                    // TODO: Previous method to encrypt
                    //String encryptedText = AESUtilities.encrypt(unencryptedText);
                    String encryptedText = AES256Cryptor.encrypt(unencryptedText, sharedSecret);

                    WebSocketHandler.getSocket().sendMessage(new JSONConstructor().constructTextJSON(UserDetails.username, UserDetails.chatWith, encryptedText));

                    addMessageBox(messageToSend.getText().toString(), "",1);

                    //Thread.sleep(2000);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                messageToSend.getText().clear();
            }
        });

        // Start the handler and run it every 1 second
        handler.postDelayed(runnable, 1000);

        // Scroll to the bottom of the chat for the latest messages
        scrollToTheBottom();
    }

    public void getMsg() {
        System.out.println("Inside getMsg");
        // If there are new messages for this chat, display them

        // TODO: Gather the last N messages from the local database for this chat

        if(UserDetails.messages.containsKey(UserDetails.chatWith)) {
            System.out.println(UserDetails.messages);
            for(int i = 0; i < UserDetails.messages.get(UserDetails.chatWith).size(); i++) {
                String receivedMessage = "[encrypted]";

                try {
                    // TODO: Previous method of decrypting with AES
                    //receivedMessage = AESUtilities.decrypt(UserDetails.messages.get(UserDetails.chatWith).get(i));
                    System.out.println("decrypt = " + AESUtilities.decrypt("xgMHrdWVlsyDve3Pg8S1Ww=="));

                    receivedMessage = AES256Cryptor.decrypt(UserDetails.messages.get(UserDetails.chatWith).get(i), sharedSecret);

                } catch (NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException |
                        InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException |
                        IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
                addMessageBox(receivedMessage, "",2);
            }
            UserDetails.messages.remove(UserDetails.chatWith);

            // Scroll to the bottom of the chat when getting new messages
            scrollToTheBottom();
        }
    }

    public void addMessageBox(String message, String timestamp, int type) {
        TextView textView = new TextView(Chat.this);

        String resourceText = "";
        SpannableString sS;
        // start and end are used as delimiters for different styles within SpannableString
        int start = -1, end = -1;

        // If the message has been sent now / has been received now
        if(timestamp.isEmpty()) {
            resourceText = "Now: " + message;
            start = 5;
            end = 4;
        }
        else {
            // If the message has been sent earlier / received earlier
            // Take the hh:mm using substring from timestamp
            resourceText = timestamp.substring(11, 16) + ": " + message;
            start = 7;
            end = 6;
        }

        sS = new SpannableString(resourceText);

        sS.setSpan(new ForegroundColorSpan(Color.GRAY), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sS.setSpan(new RelativeSizeSpan(0.7f), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sS.setSpan(new ForegroundColorSpan(Color.BLACK), start, sS.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if(type == 1)
            sS.setSpan(new BackgroundColorSpan(Color.rgb	(220, 248, 198)), start, sS.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if(type == 2)
            sS.setSpan(new BackgroundColorSpan(Color.rgb(198,220,248)), start, sS.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder sSBuilder = new SpannableStringBuilder();
        sSBuilder.append(sS);

        // Set the text inside the textView with the styles built using SpannableString
        textView.setText(sSBuilder);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 17.0f;
        lp2.bottomMargin = 10;
        //textView.setTextColor(Color.parseColor("black"));

        // Calculate the width of the screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        if(type == 1) {
            lp2.gravity = Gravity.LEFT;
            lp2.leftMargin = 20;
            lp2.topMargin = 5;
            // Display the text up until half of the screen width
            lp2.rightMargin = width/2;
            //textView.setBackgroundColor(Color.parseColor("#06BD75"));
            //textView.setBackgroundResource(R.drawable.outgoing_message);
        }
        else {
            lp2.gravity = Gravity.RIGHT;
            lp2.topMargin = 5;
            lp2.rightMargin = 10;
            // Display the text up until half of the screen width
            lp2.leftMargin = width/2;
            //textView.setBackgroundColor(Color.parseColor("#33C4FF"));
            //textView.setBackgroundResource(R.drawable.incoming_message);
        }
        textView.setLayoutParams(lp2);
        linearLayoutScrollView.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public void showHistoryOfMessages(String sender, String recipient, String numberOfDays) {

        try {
            WebSocketHandler.getSocket().sendMessageAndWait(new JSONConstructor().constructGetChatHistory(sender, recipient, numberOfDays));

            // Get response from server and parse it
            JSONObject responseJSON = new JSONObject(WebSocketHandler.getSocket().getResponse());
            if (responseJSON.get("REPLY").toString().equalsIgnoreCase("GETCHATHISTORY: SUCCESS")) {

                // If there were messages found on the server for this chat within last numberOfDays days
                if(responseJSON.has("messages")) {

                    // First try to get an array of objects (messages) from the server
                    try {
                        JSONArray jsonArr = responseJSON.getJSONArray("messages");

                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject x = jsonArr.getJSONObject(i);

                            // If message at iteration i was send by this user, display it on the left side with appropriate color
                            if (x.get("sender").toString().equalsIgnoreCase(UserDetails.username)) {
                                //addMessageBox(AESUtilities.decrypt(x.get("message").toString()), x.get("timestamp").toString(), 1);
                                addMessageBox(AES256Cryptor.decrypt(x.get("message").toString(), sharedSecret), x.get("timestamp").toString(), 1);
                            }

                            // If message at iteration i was send by the other user, display it on the right side with appropriate color
                            if (x.get("recipient").toString().equalsIgnoreCase(UserDetails.username)) {
                                //addMessageBox(AESUtilities.decrypt(x.get("message").toString()), x.get("timestamp").toString(),2);
                                addMessageBox(AES256Cryptor.decrypt(x.get("message").toString(), sharedSecret), x.get("timestamp").toString(),2);
                            }
                        }

                    } catch(JSONException e) {
                        // If there is only one message found, returned JSON will not contain '[]'
                        // and therefore will not be an array, so it will be caught here
                        JSONObject x = responseJSON.getJSONObject("messages");

                        // If message at iteration i was send by this user, display it on the left side with appropriate color
                        if (x.get("sender").toString().equalsIgnoreCase(UserDetails.username)) {
                            //addMessageBox(AESUtilities.decrypt(x.get("message").toString()), x.get("timestamp").toString(), 1);
                            addMessageBox(AES256Cryptor.decrypt(x.get("message").toString(), sharedSecret), x.get("timestamp").toString(), 1);
                        }

                        // If message at iteration i was send by the other user, display it on the right side with appropriate color
                        if (x.get("recipient").toString().equalsIgnoreCase(UserDetails.username)) {
                            //addMessageBox(AESUtilities.decrypt(x.get("message").toString()), x.get("timestamp").toString(), 2);
                            addMessageBox(AES256Cryptor.decrypt(x.get("message").toString(), sharedSecret), x.get("timestamp").toString(), 2);
                        }
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // If received messages when the user was not in chat, after GETCHATHISTORY the last messages
        // while offline will be duplicated. In this case, delete them as GETCHATHISTORY will retrieve them for us
        if(UserDetails.messages.containsKey(UserDetails.chatWith))
            UserDetails.messages.remove(UserDetails.chatWith);

    }

    public void scrollToTheBottom() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                // Following print is to debug if the new Runnable executes more than once
                System.out.println("ScrollView post test");
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void getSharedSecretKey() {
        class GetSharedSecretKey extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                // SELECT shared_secret_key FROM storedchatlist WHERE username LIKE :userToSearchFor AND chat_belongs_to LIKE :myself
                System.out.println("Getting secret shared key (doInBackground)");
                sharedSecret = AppDatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .storedChatListDao()
                        .getSharedKeyFromChatList(UserDetails.chatWith, UserDetails.username);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                // Refresh
                System.out.println("Getting secret shared key (onPostExecute)");
            }
        }

        GetSharedSecretKey gt = new GetSharedSecretKey();
        gt.execute();
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        /*
         *  Disable the back button in the current screen by not calling super
         *
         *  This is important in order to not allow the user to see sensitive information
         *  from other users that we're previously logged into this device
         *
         *  We are calling onSupportNavigateUp method instead
         */
        onSupportNavigateUp();
    }

}
