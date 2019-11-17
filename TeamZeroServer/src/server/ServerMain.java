package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.*;
import java.util.logging.*;

/**
 * Main Server class and system entry point.
 */
public class ServerMain extends WebSocketServer {

	private static final Logger LOGGER = Logger.getLogger("ServerLog");
	
	/*
	 * Logging level to control Console log output. Set to All to see everything.
	 * Set to FINE to see everything except messages sent from server
	 * Set to INFO to only see highest level connection and error info
	 */
	private static final Level LOG_LEVEL = Level.ALL;

	private static final int LOCAL_PORT = 1234;
	
	/**
	 * Message type information indicators for clients to put in first JSON element.
	 * eg: { type: "REGISTER", ...}
	 */
	private static final String CASE_REGISTER = "REGISTER"; // register new client
	
	private static final String CASE_TEXT_MESSAGE = "TEXT"; // send text to another client 
	
	private static final String CASE_UNREGISTER = "UNREGISTER"; // delete client from system
	
	private static final String CASE_LOGIN = "LOGIN"; //login with user name and password

	private static final String CASE_EDIT_PROFILE = "EDIT"; //edit profile details

	private static final String MESSAGE_REPLY_SUCCESS = "SUCCESS"; //feedback reply to clients 
	private static final String MESSAGE_REPLY_FAILED = "FAILED"; //feedback reply to clients 
	
	private static final String MESSAGE_REPLY = "REPLY"; //feedback reply to clients 
	
	/**
	 * JSON keys or attributes that the server expects within data sent from the client
	 */
	private static final String JSON_KEY_MESSAGE_TYPE = "type";

	private static final String JSON_KEY_USERNAME = "username";

	private static final String JSON_KEY_PASSWORD = "password";
	
	private static final String JSON_KEY_MESSAGE  = "message";

	private static final String JSON_KEY_SENDER = "sender";
	
	private static final String JSON_KEY_RECIPIENT = "recipient";
	
	private static final String JSON_KEY_EMAIL = "email";
	
	/**
	 * A map of Connections to the client IDs of the clients they connect to
	 */
	private HashMap<WebSocket, Integer> clientWebSockets;
	
	
	/**
	 * A map of recipient client username to a list of messages that still need to be sent to them
	 */
	private HashMap<String, ArrayList<String>> recipientUnsentMessages;
	
	
	/**
	 * System entry point
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException {
		setupLogger();
		LOGGER.log( Level.INFO, "System started");
		startServer();
	}
	
	/**
	 * Sets up the logger and handlers so that information can be logged to the console
	 * and an external text file 
	 */
	private static void setupLogger() {
		  // create console handler to output to console
		 ConsoleHandler consoleHandler = new ConsoleHandler();
		 consoleHandler.setLevel(LOG_LEVEL);
		 LOGGER.addHandler(consoleHandler);
		 LOGGER.setLevel(Level.ALL);
		 LOGGER.setUseParentHandlers(false);	
		 // create file handler to output to file
		Handler fileHandler;
		try {
			fileHandler = new FileHandler("ServerLog.txt",false);
			 SimpleFormatter plainText = new SimpleFormatter();
			 fileHandler.setFormatter(plainText);
			 fileHandler.setLevel(Level.ALL);
			 LOGGER.addHandler(fileHandler);
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.WARNING, "Could not create Server Log file.");
		} 
	}
	
	/**
	 * Constructs a ServerMain object, and initializes the map of web socket connections to client IDs
	 * @param port
	 * @throws UnknownHostException
	 */
	public ServerMain(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
		clientWebSockets = new HashMap<WebSocket, Integer>();
		recipientUnsentMessages = new HashMap<String, ArrayList<String>>();
	}
	
	private static void startServer() throws UnknownHostException {
		int port;
		try {
			// get port information on heroku
			port = Integer.parseInt(System.getenv("PORT"));
		}catch(NumberFormatException e) {
			port = LOCAL_PORT;
		}
		LOGGER.log( Level.INFO, "Port is {0}", String.valueOf(port));
		new ServerMain(port).start();
	}

	@Override
	public void onClose(WebSocket websocket, int arg1, String arg2, boolean arg3) {
		
		// remove client from connected clients manager and from client websockets array
		int clientId = clientWebSockets.get(websocket);
		ClientManager.getInstance().removeClient(clientId);
		clientWebSockets.remove(websocket);
		LOGGER.log( Level.INFO, "Client connection closed: {0}", websocket.toString());	
	}

	@Override
	public void onError(WebSocket arg0, Exception arg1) {
		LOGGER.log( Level.WARNING, "Server onError called: {0}", arg1.getMessage());
	}

	/**
	 * Called when a message is received from a remote host. Expected message is in JSON format.
	 */
	@Override
	public void onMessage(WebSocket websocket, String message) {
		LOGGER.log( Level.FINE, "Message received from　client: {0}", message);
		
		//parse JSON and get first element to check type
		try {
			JSONObject json = new JSONObject(message);
			String msgType = json.getString(JSON_KEY_MESSAGE_TYPE);
			if (msgType.equals(CASE_LOGIN)) {
				String userName = json.getString(JSON_KEY_USERNAME);
				String password = json.getString(JSON_KEY_PASSWORD);

				DbConnection dbConnection = new DbConnection();
				Client authenticatedClient = dbConnection.authenticateUser(userName, password);
				websocket.send(userName + "Logged in.");
				// if the client is authenticated, get their info and add to connected clients
				if (authenticatedClient != null) {
					//tell user login was successful
					websocket.send(generateReplyToClient(CASE_LOGIN, MESSAGE_REPLY_SUCCESS, ""));
					
					authenticatedClient.setLoggedIn(true); // setloggedInflag
					ClientManager.getInstance().addClient(authenticatedClient);
					int id = authenticatedClient.getId();
					clientWebSockets.put(websocket, id);
					// check if there are any unsent messages to send & send them
					sendUnsentMessages(websocket, userName);
				}
				else {
					LOGGER.log( Level.FINE, 
							"Cannot authenticate username ({0}) and password ({1})", 
							new String[] {userName,password});
					websocket.send((generateReplyToClient(
										CASE_LOGIN,
										MESSAGE_REPLY_FAILED, 
										"Cannot authenticate user. Check login details.")));
				}
								
			}
			else if (msgType.equals(CASE_REGISTER)) {
				// TODO save pictures
				String userName = json.getString(JSON_KEY_USERNAME);
				String password = json.getString(JSON_KEY_PASSWORD);
				String email = json.getString(JSON_KEY_EMAIL);
				DbConnection dbConnection = new DbConnection();
				boolean successful = dbConnection.addUser(userName, password, email);
				if (successful) {
					websocket.send(generateReplyToClient(CASE_REGISTER, MESSAGE_REPLY_SUCCESS, ""));
				}
				else {
					// TODO need to get message from DB
					websocket.send(generateReplyToClient(CASE_REGISTER, MESSAGE_REPLY_FAILED, ""));
				}
			}
			else if (msgType.equals(CASE_TEXT_MESSAGE)) {
				// check if user is logged in first
				if (isAuthenticated(websocket)) {
					String sender = json.getString(JSON_KEY_SENDER);	
					String recipient = json.getString(JSON_KEY_RECIPIENT);
					String textMessage = json.getString(JSON_KEY_MESSAGE);
					DbConnection dbConnection = new DbConnection();
					dbConnection.addMessage(sender, recipient, textMessage);
					sendClientText(websocket, json);
				}
				else {
					LOGGER.log( Level.FINE, 
							"Cannot send client message - sender is not logged in. {0}", websocket.toString());
					websocket.send(
							generateReplyToClient(
									CASE_TEXT_MESSAGE,
									MESSAGE_REPLY_FAILED, 
									"Sender is not logged in"));
				}
			}
			else if (msgType.equals(CASE_UNREGISTER)) {
				// TODO check if logged in first
				String userName = json.getString(JSON_KEY_USERNAME);
				String password = json.getString(JSON_KEY_PASSWORD);
				DbConnection dbConnection = new DbConnection();
				boolean success = dbConnection.deleteUser(userName, password);
				if (success) {
					websocket.send(generateReplyToClient(CASE_UNREGISTER, MESSAGE_REPLY_SUCCESS, ""));		
					// remove user from connected lists
					int clientId = clientWebSockets.get(websocket);
					ClientManager.getInstance().removeClient(clientId);
					clientWebSockets.remove(websocket);
				}
				else {
					websocket.send(generateReplyToClient(CASE_UNREGISTER, MESSAGE_REPLY_FAILED, ""));
				}
			}
			else if (msgType.equals(CASE_EDIT_PROFILE)) {
				// TODO edit profile
				// check if logged in first
			}
			else {
				websocket.send(generateReplyToClient(msgType, MESSAGE_REPLY_FAILED, "unrecognised type"));
			}
		} catch (JSONException e) {
			websocket.send(generateReplyToClient("", MESSAGE_REPLY_FAILED, "Bad JSON Format: " + e.getMessage()));
			e.printStackTrace();
		}
	}

	/**
	 * Called when a new connection is established with a remote host.
	 */
	@Override
	public void onOpen(WebSocket websocket, ClientHandshake handshake) {
		
		LOGGER.log( Level.INFO, 
				"Connection established with a client: {0}", websocket.toString());
	}
	
	/**
	 * checks if the websocket associated with a client has previously logged in
	 * @param websocket
	 * @return
	 */
	private boolean isAuthenticated(WebSocket websocket) {
		int id = clientWebSockets.get(websocket);
		Client c = ClientManager.getInstance().getClientById(id);
		if (c == null) return false;
		return  c.isLoggedIn();
	}
	
	
	/**
	 * Send a text message from the sender (websocket) to the recipient stated within the JSON message
	 * @param websocket
	 * @param message
	 * @throws JSONException
	 */
	private void sendClientText(WebSocket websocket, JSONObject message) throws JSONException {
		/*
		 * expecting the following json

			{
				type: TEXT
				sender: fromUsername
				recipient: toUsername
				message: message
			}
		*/
		String recipientUsername = message.getString(JSON_KEY_RECIPIENT);
		Client recipient = ClientManager.getInstance().getClientByUsername(recipientUsername);
		WebSocket recipientSocket = null;
		
		if (recipient != null) {
			recipientSocket = getWebSocketByClientId(recipient.getId());
		}
		
		if (recipientSocket != null) {
			// if recipient is connected, send message right away
				recipientSocket.send(message.toString());
		}
		else {
			// if recipient is not currently connected
			// insert message into unsent messages map
	
			ArrayList<String> unsentMessages;

			// check if there already are any unsent messages
			if (recipientUnsentMessages.containsKey(recipientUsername)){
				unsentMessages = recipientUnsentMessages.get(recipientUsername);
				unsentMessages.add(message.toString());
				
				// but the updated array list back in the map
				recipientUnsentMessages.put(recipientUsername, unsentMessages);
			}
			else { 
				// if there are no previous messages, add a new array with one message
				unsentMessages = new ArrayList<String>();
				unsentMessages.add(message.toString());
				recipientUnsentMessages.put(recipientUsername, unsentMessages);	
			}
			
		}		
	}
	
	/**
	 * Search for the web socket associated with a client ID within the connected clients hash map
	 * @param id
	 * @return
	 */
	private WebSocket getWebSocketByClientId(int id) {
		
		Iterator<Entry<WebSocket, Integer>> clientIterator = clientWebSockets.entrySet().iterator();
		Entry<WebSocket, Integer> nextSet = null;
		int nextId = 0;
		while(clientIterator.hasNext()) {
			nextSet = clientIterator.next();
			nextId = nextSet.getValue();
			if (nextId == id) {
				// return the web socket that was the key for the client ID value in the hash map
				return nextSet.getKey(); 
			}
		}
		return null;
	}
	
	/**
	 * Generates a JSON reply message for clients given the original request type,
	 * the feedback (success or failure) and an optional message to append
	 * @param originalType
	 * @param feedback
	 * @param message
	 * @return a JSON string in the format {"type":"reply","reply":originalType: feedback,"message":message}
	 */
	private String generateReplyToClient(String originalType, String feedback, String message) {
		// using StringBuilder instead of JSONObject because 
		//JSONObject seems to create unnecessary arrays
		StringBuilder sb = new StringBuilder();	
		sb.append("{\"").append(JSON_KEY_MESSAGE_TYPE)
				.append("\":\"").append(MESSAGE_REPLY).append("\",");
		sb.append("\"").append(MESSAGE_REPLY).append("\":\"")
			.append(originalType).append(": ").append(feedback).append("\",");	
		sb.append("\"").append(JSON_KEY_MESSAGE).append("\":\"")
			.append(message).append("\"}");
		
		LOGGER.log(Level.FINER, "Sending reply: {0}", sb.toString());
		return sb.toString();
	}
	
	
	/**
	 * Send messages that were previously unsent to a given web socket. Intended to be called
	 * when a client has newly logged in or reconnected.
	 * @param websocket 
	 */
	private void sendUnsentMessages(WebSocket websocket, String recipientUsername) {
		ArrayList<String> messagesToSend = recipientUnsentMessages.get(recipientUsername);
		
		// if there are no messages waiting, simply return
		if (messagesToSend == null)
		{
			return;
		}
		
		Iterator<String> iterator = messagesToSend.iterator();
		//TODO: currently sends as separate messages. it may be preferable to send a single larger
		// message with all the data?
		while(iterator.hasNext()) {
			String nextMessage = iterator.next();
			LOGGER.log(Level.FINER, 
					"Sending user {0} previously unsent message: {1}",
					new String[] {recipientUsername, nextMessage});
			websocket.send(nextMessage);
		}
		// delete the messages as they no longer require sending
		recipientUnsentMessages.remove(recipientUsername);
	}

	
	private void editClientProfile (WebSocket websocket, JSONObject message) {
		// TODO edit client profile (picture?)
	}
}
