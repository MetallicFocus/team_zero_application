package teamzero.chat.mobile;

import android.app.ProgressDialog;

import android.content.Intent;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ChatList extends AppCompatActivity {

    ListView usersList;
    TextView noChatsFoundTextDisplay;
    FloatingActionButton newChatBtn;

    ArrayList<HashMap<String, String>> chatList = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;

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

        // TODO: Change URL to access JSON content from our server
        String url = "https://api.myjson.com/bins/lj6f8";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(ChatList.this);
        rQueue.add(request);

        // When the user clicks on a specific chat from his history, go to that conversation
        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.chatWith = chatList.get(position).get("username");
                Snackbar.make(view, "Currently Under Development . . . Chat with " + UserDetails.chatWith, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // TODO: Start Activity --> Goto chat page with X person
            }
        });

        // When a user clicks on the "new chat" button, go to the "new chat" page
        newChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Currently Under Development . . . New Chat Feature", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // TODO: Start Activity --> Goto new chat page
            }
        });
    }

    public void doOnSuccess(String s) {

        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            HashMap<String, String> specificUserDetails;

            while(i.hasNext()){
                specificUserDetails = new HashMap<>();
                key = i.next().toString();
                // Populate the hashmap representing chat list details to a specific chat from the list
                specificUserDetails.put("username", key);
                specificUserDetails.put("last_message", obj.getJSONObject(key).getString("last_message"));
                // Add a chat from the list to the final array
                chatList.add(specificUserDetails);
                totalUsers++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // If there are no chats with other users found in history, display a suggestive text
        if(totalUsers < 1) {
            noChatsFoundTextDisplay.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        }

        else {
            noChatsFoundTextDisplay.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);

            // Overridden the getView of ArrayAdapter in order to access both text1 and text2 (from simple_list_item_2) and write on them right away
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, chatList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                    text1.setText(chatList.get(position).get("username"));
                    text2.setText(chatList.get(position).get("last_message"));
                    return view;
                }
            };
            usersList.setAdapter(adapter);
        }

        pd.dismiss();
    }
}