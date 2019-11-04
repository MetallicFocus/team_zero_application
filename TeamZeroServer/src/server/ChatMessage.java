package server;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * Encapsulates a single chat message sent from one client to another client.
 */
public class ChatMessage {
	

	private String senderUsername;
	private Date timeSent;
	
	private String recipientUsername;
	
	private String message;


	/**
	 * Initiate a chat message instance
	 * @param senderUsername User name of the sender
	 * @param recipientUsername user name of the recipient 
	 * @param message
	 */
	public ChatMessage(String senderUsername, String recipientUsername, String message, Date timeSent){
		this.senderUsername = senderUsername;
		this.recipientUsername = recipientUsername;
		this.message = message;
		this.setTimeSent(timeSent);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String setSenderUsername() {
		return senderUsername;
	}

	public void setSenderUsername(String senderUsername) {
		this.senderUsername = senderUsername;
	}

	public String getRecipientUsername() {
		return recipientUsername;
	}

	public void setRecipientUsername(String recipientUsername) {
		this.recipientUsername = recipientUsername;
	}
	
	
	/**
	 * Produces a JSONObject with the sender, recipient, and message
	 * @return A JSONObject with all chat message data
	 * @throws JSONException
	 */
	public JSONObject toJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.append("sender", senderUsername);
		json.append("recipient", recipientUsername);
		json.append("message", message);
		return json;
		
	}

	public Date getTimeSent() {
		return timeSent;
	}

	public void setTimeSent(Date timeSent) {
		this.timeSent = timeSent;
	}
	
	
	
	
	

}
