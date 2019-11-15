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

    public String constructRegisterJSON(String username, String password, String email, Object picture) throws JSONException {

        jsonObject.put("type", "REGISTER");
        jsonObject.put("username", username);
        jsonObject.put("password", password);
        jsonObject.put("email", email);
        jsonObject.put("picture", picture);

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

    public String constructEditProfileJSON(String username, Object newPicture) throws JSONException {

        jsonObject.put("type", "EDIT");
        jsonObject.put("username", username);
        jsonObject.put("newPicture", newPicture);

        builtJSON = jsonObject.toString();
        return builtJSON;
    }

}
