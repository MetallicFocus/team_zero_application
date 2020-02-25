package tools;

/*
*
* Tool based on JSONObject used to construct server-specific JSON requests
*
*/

import org.json.JSONException;
import org.json.JSONObject;

public class JSONConstructor {

    private String builtJSON;
    private JSONObject jsonObject = new JSONObject();

    public String constructRegisterJSON(String username, String password, String email, Object picture, String publicKey) throws JSONException {

        jsonObject.put("type", "REGISTER");
        jsonObject.put("username", username);
        jsonObject.put("password", password);
        jsonObject.put("email", email);
        jsonObject.put("picture", picture);
        jsonObject.put("publicKey", publicKey);

        builtJSON = jsonObject.toString();
        return builtJSON;
    }

    public String constructLoginJSON(String username, String password) throws JSONException {

        jsonObject.put("type", "LOGIN");
        jsonObject.put("username", username);
        jsonObject.put("password", password);

        builtJSON = jsonObject.toString();
        return builtJSON;
    }

    public String constructTextJSON(String sender, String recipient, String message) throws JSONException {
        jsonObject.put("type", "TEXT");
        jsonObject.put("sender", sender);
        jsonObject.put("recipient", recipient);
        jsonObject.put("message", message);

        builtJSON = jsonObject.toString();
        return builtJSON;
    }

    public String constructUnregisterJSON(String username, String password) throws JSONException {

        jsonObject.put("type", "UNREGISTER");
        jsonObject.put("username", username);
        jsonObject.put("password", password);

        builtJSON = jsonObject.toString();
        return builtJSON;
    }

    public String constructEditProfileJSON(String username, Object newPicture, String publicKey) throws JSONException {

        jsonObject.put("type", "EDIT");
        jsonObject.put("username", username);
        jsonObject.put("newPicture", newPicture);
        jsonObject.put("publicKey", publicKey);

        builtJSON = jsonObject.toString();
        return builtJSON;
    }

    public String constructGetAllContactsJSON() throws JSONException {

        jsonObject.put("type", "GETALLCONTACTS");

        builtJSON = jsonObject.toString();
        return builtJSON;
    }

    public String constructSearchContactsJSON(String searchForUsername) throws JSONException {

        jsonObject.put("type", "SEARCHCONTACTS");
        jsonObject.put("search", searchForUsername);

        builtJSON = jsonObject.toString();
        return builtJSON;
    }

    public String constructGetPublicKeyJSON(String username) throws JSONException {

        jsonObject.put("type", "GETPUBLICKEY");
        jsonObject.put("username", username);

        builtJSON = jsonObject.toString();
        return builtJSON;
    }

    public String constructGetChatHistory(String myUsername, String theirUsername, String numberOfDays) throws JSONException {

        jsonObject.put("type", "GETCHATHISTORY");
        jsonObject.put("myUsername", myUsername);
        jsonObject.put("theirUsername", theirUsername);
        jsonObject.put("historyDays", numberOfDays);

        builtJSON = jsonObject.toString();
        return builtJSON;
    }

}
