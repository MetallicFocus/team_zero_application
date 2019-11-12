package server;

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

/**
 * Main Server class and system entry point.
 */
public class ServerMain extends WebSocketServer {


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
	 * A map of recipient client IDs to a list of messages that still need to be sent to them
	 */
	private HashMap<Integer, ArrayList<String>> recipientUnsentMessages;
	
	
	/**
	 * System entry point
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException {
		System.out.println("System started");
		startServer();
	}
	
	/**
	 * Constructs a ServerMain object, and initializes the map of web socket connections to client IDs
	 * @param port
	 * @throws UnknownHostException
	 */
	public ServerMain(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
		clientWebSockets = new HashMap<WebSocket, Integer>();
		recipientUnsentMessages = new HashMap<Integer, ArrayList<String>>();
	}
	
	private static void startServer() throws UnknownHostException {
		int port;
		
		try {
			// get port information on heroku
			port = Integer.parseInt(System.getenv("PORT"));
			System.out.println("port is" + port);
		}catch(NumberFormatException e) {
			port = LOCAL_PORT;
		}
		new ServerMain(port).start();
	}

	@Override
	public void onClose(WebSocket websocket, int arg1, String arg2, boolean arg3) {
		
		// remove client from connected clients manager and from client websockets array
		ClientManager.getInstance().removeClient(clientWebSockets.get(websocket));
		clientWebSockets.remove(websocket);
		System.out.println("Client connection closed: " + websocket.toString()); 
		
	}

	@Override
	public void onError(WebSocket arg0, Exception arg1) {
		System.out.println(arg1.toString());
		
	}

	/**
	 * Called when a message is received from a remote host. Expected message is in JSON format.
	 */
	@Override
	public void onMessage(WebSocket websocket, String message) {
		System.out.println(message); 

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
					authenticatedClient.setLoggedIn(true); // setloggedInflag
					ClientManager.getInstance().addClient(authenticatedClient);
					int id = authenticatedClient.getId();
					clientWebSockets.put(websocket, id);
					// check if there are any unsent messages to send & send them
					
//					sendUnsentMessages(websocket, id);
					
				}
				else {
					//TODO cant authenticate, send error response
				}
								
			}
			else if (msgType.equals(CASE_REGISTER)) {
				// TODO save pictures
				String userName = json.getString(JSON_KEY_USERNAME);
				String password = json.getString(JSON_KEY_PASSWORD);
				String email = json.getString(JSON_KEY_EMAIL);
				DbConnection dbConnection = new DbConnection();
				dbConnection.addUser(userName, password, email);
				websocket.send(userName + " registered.");
			}
			else if (msgType.equals(CASE_TEXT_MESSAGE)) {
				String sender = json.getString(JSON_KEY_SENDER);
				String recipient = json.getString(JSON_KEY_RECIPIENT);
				String textMessage = json.getString(JSON_KEY_MESSAGE);
				DbConnection dbConnection = new DbConnection();
				dbConnection.addMessage(sender, recipient, textMessage);
				// check if user is logged in first
				if (isAuthenticated(websocket)) {
					sendClientText(websocket, json);
				}
				else {
					// TODO send back error message
					System.out.println("Cannot send client message - sender is not logged in.");
				}
			}
			else if (msgType.equals(CASE_UNREGISTER)) {
				// TODO check if logged in first
				String userName = json.getString(JSON_KEY_USERNAME);
				String password = json.getString(JSON_KEY_PASSWORD);
				DbConnection dbConnection = new DbConnection();
				dbConnection.deleteUser(userName, password);
			}
			else if (msgType.equals(CASE_EDIT_PROFILE)) {
				// TODO edit profile
				// check if logged in first
			}
			else {
				// TODO send error - unknown 
				websocket.send(message);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Called when a new connection is established with a remote host.
	 */
	@Override
	public void onOpen(WebSocket websocket, ClientHandshake handshake) {
		
		System.out.println("Connection established with a client.");
		System.out.println("WebSocket: " + websocket.toString());
		System.out.println("Handshake: " + handshake.toString());
		
		//TODO check if its the same as a previously logged in client so we dont
		// have to authenticate again
		
		
		
	}
	
	/**
	 * checks if the websocket associated with a client has previously logged in
	 * @param websocket
	 * @return
	 */
	private boolean isAuthenticated(WebSocket websocket) {
		Client c = ClientManager.getInstance().getClientById(clientWebSockets.get(websocket));
		return c.isLoggedIn();
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
			
			int rId = recipient.getId();
			ArrayList<String> unsentMessages;

			// check if there already are any unsent messages
			if (recipientUnsentMessages.containsKey(rId)){
				unsentMessages = recipientUnsentMessages.get(rId);
				unsentMessages.add(message.toString());
				
				// but the updated array list back in the map
				recipientUnsentMessages.put(rId, unsentMessages);
			}
			else { 
				// if there are no previous messages, add a new array with one message
				unsentMessages = new ArrayList<String>();
				unsentMessages.add(message.toString());
				recipientUnsentMessages.put(rId, unsentMessages);	
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
	 * Send messages that were previously unsent to a given web socket. Intended to be called
	 * when a client has newly logged in or reconnected.
	 * @param websocket 
	 */
	private void sendUnsentMessages(WebSocket websocket, int recipientId) {
		ArrayList<String> messagesToSend = recipientUnsentMessages.get(recipientId);
		Iterator<String> iterator = messagesToSend.iterator();
		
		//TODO: currently sends as separate messages. it may be preferable to send a single larger
		// message with all the data?
		while(iterator.hasNext()) {
			String nextMessage = iterator.next();
			websocket.send(nextMessage);
		}
	}

	
	private void editClientProfile (WebSocket websocket, JSONObject message) {
		// TODO edit client profile (picture?)
	}
}
