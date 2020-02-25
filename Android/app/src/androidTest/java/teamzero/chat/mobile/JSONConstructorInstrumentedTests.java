package teamzero.chat.mobile;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import tools.JSONConstructor;

import static org.junit.Assert.*;

/**
 *
 * Instrumented tests (which will execute on an Android device) for JSONConstructor tool
 *
 */
@RunWith(AndroidJUnit4.class)
public class JSONConstructorInstrumentedTests {

    @Test
    public void registrationJSONisValid() {
        JSONConstructor jsonConstructor = new JSONConstructor();
        try {
            assertEquals(null, "{\"type\":\"REGISTER\",\"username\":\"UsernameTest\",\"password\":\"PwdTest\",\"email\":\"testEmail@gmail.com\",\"picture\":\"null\",\"publicKey\":\"testPublicKey\"}", jsonConstructor.constructRegisterJSON("UsernameTest", "PwdTest", "testEmail@gmail.com", "null", "testPublicKey"));
            assertEquals(null, "{\"type\":\"REGISTER\",\"username\":\"UsernameTest\",\"password\":\"PwdTest\",\"email\":\"testEmail@gmail.com\"}", jsonConstructor.constructRegisterJSON("UsernameTest", "PwdTest", "testEmail@gmail.com", null, null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loginJSONisValid() {
        JSONConstructor jsonConstructor = new JSONConstructor();
        try {
            assertEquals(null, "{\"type\":\"LOGIN\",\"username\":\"UsernameTest\",\"password\":\"PwdTest\"}", jsonConstructor.constructLoginJSON("UsernameTest", "PwdTest"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void textJSONisValid() {
        JSONConstructor jsonConstructor = new JSONConstructor();
        try {
            assertEquals(null, "{\"type\":\"TEXT\",\"sender\":\"UsernameTest1\",\"recipient\":\"UsernameTest2\",\"message\":\"Hey there!\"}", jsonConstructor.constructTextJSON("UsernameTest1", "UsernameTest2", "Hey there!"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void unregisterJSONisValid() {
        JSONConstructor jsonConstructor = new JSONConstructor();
        try {
            assertEquals(null, "{\"type\":\"UNREGISTER\",\"username\":\"UsernameTest\",\"password\":\"PwdTest\"}", jsonConstructor.constructUnregisterJSON("UsernameTest", "PwdTest"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void editProfileJSONisValid() {
        JSONConstructor jsonConstructor = new JSONConstructor();
        try {
            assertEquals(null, "{\"type\":\"EDIT\",\"username\":\"UsernameTest\",\"newPicture\":\"null\",\"publicKey\":\"testPublicKey\"}", jsonConstructor.constructEditProfileJSON("UsernameTest", "null", "testPublicKey"));
            assertEquals(null, "{\"type\":\"EDIT\",\"username\":\"UsernameTest\"}", jsonConstructor.constructEditProfileJSON("UsernameTest", null, null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllContactsJSONisValid() {
        JSONConstructor jsonConstructor = new JSONConstructor();
        try {
            assertEquals(null, "{\"type\":\"GETALLCONTACTS\"}", jsonConstructor.constructGetAllContactsJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void searchContactsJSONisValid() {
        JSONConstructor jsonConstructor = new JSONConstructor();
        try {
            assertEquals(null, "{\"type\":\"SEARCHCONTACTS\",\"search\":\"UsernameTest\"}", jsonConstructor.constructSearchContactsJSON("UsernameTest"));
            assertEquals(null, "{\"type\":\"SEARCHCONTACTS\"}", jsonConstructor.constructSearchContactsJSON(null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getPublicKeyJSONisValid() {
        JSONConstructor jsonConstructor = new JSONConstructor();
        try {
            assertEquals(null, "{\"type\":\"GETPUBLICKEY\",\"username\":\"UsernameTest\"}", jsonConstructor.constructGetPublicKeyJSON("UsernameTest"));
            assertEquals(null, "{\"type\":\"GETPUBLICKEY\"}", jsonConstructor.constructGetPublicKeyJSON(null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getChatHistoryJSONisValid() {
        JSONConstructor jsonConstructor = new JSONConstructor();
        try {
            assertEquals(null, "{\"type\":\"GETCHATHISTORY\",\"myUsername\":\"UsernameTest\",\"theirUsername\":\"usernameTwo\",\"historyDays\":\"1\"}", jsonConstructor.constructGetChatHistory("UsernameTest", "usernameTwo", "1"));
            assertEquals(null, "{\"type\":\"GETCHATHISTORY\",\"myUsername\":\"UsernameTest\",\"theirUsername\":\"usernameTwo\"}", jsonConstructor.constructGetChatHistory("UsernameTest", "usernameTwo", null));
            assertEquals(null, "{\"type\":\"GETCHATHISTORY\",\"myUsername\":\"UsernameTest\"}", jsonConstructor.constructGetChatHistory("UsernameTest", null, null));
            assertEquals(null, "{\"type\":\"GETCHATHISTORY\"}", jsonConstructor.constructGetChatHistory(null, null, null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
