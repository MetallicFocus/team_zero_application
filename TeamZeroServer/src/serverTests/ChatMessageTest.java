package serverTests;
import server.ChatMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import org.junit.jupiter.api.Test;

import org.junit.Assert;

class ChatMessageTest {

	@Test  // test currently failing because ChatMessage is returning single item arrays as values in the JSON
	void toJson_ReturnsCorrectJsonObject() {
		String sender = "sender";
		String recipient = "recipient";
		String message = "some message";
		
		try {
		JSONObject expectedResult = new JSONObject("{\"sender\":\"" 
								+ sender + "\",\"recipient\":\""
								+ recipient + "\",\"message\":\"" 
								+ message + "\"}");
		
		ChatMessage msg = new ChatMessage(sender, recipient, message);
		
			JSONObject json = msg.toJson();
			Assert.assertEquals(expectedResult, json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		
	}
	

}
