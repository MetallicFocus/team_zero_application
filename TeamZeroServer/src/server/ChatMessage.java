package server;

import java.util.Date;

public class ChatMessage {
	private String senderUsername;
	private String recipientUsername;
	
	private String message;
	
	private String timestamp;
	
	public ChatMessage(String sender, String recipient, String message, String timestamp) {
		this.senderUsername = sender;
		this.recipientUsername = recipient;
		this.message = message;
		this.timestamp = timestamp;
	}

	public String getSenderUsername() {
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
