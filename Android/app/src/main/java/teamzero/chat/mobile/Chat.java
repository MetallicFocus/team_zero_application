package teamzero.chat.mobile;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

public class Chat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        Snackbar.make(findViewById(R.id.recyclerViewMessageList), "Currently Under Development . . . Chat with " + UserDetails.chatWith, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

}
