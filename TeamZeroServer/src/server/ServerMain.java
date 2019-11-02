package server;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
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
	
	private static final String CASE_LOGIN = "LOGIN"; //login with username and password
	
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
	 * A hashmap of Connections to the client IDs of the clients they connect to
	 */
	private HashMap<WebSocket, Integer> clientWebSockets;
	
	/**
	 * System entry point
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException {
		System.out.println("System started");
		startServer();
	}
	
	/**
	 * Constructs a ServerMain object, and initialises the hashmap of websocket connections to client IDs
	 * @param port
	 * @throws UnknownHostException
	 */
	public ServerMain(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
		clientWebSockets = new HashMap<WebSocket, Integer>();
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
		
		// Set disconnected client's isOnline field to false
		Client offlineClient = ClientManager.getInstance().getClientById(clientWebSockets.get(websocket));
		offlineClient.setOnline(false);
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
		websocket.send(message);

		//parse JSON and get first element to check type
		try {
			JSONObject json = new JSONObject(message);
			String msgType = json.getString(JSON_KEY_MESSAGE_TYPE);
			if (msgType == CASE_LOGIN) {
				// TODO check username/passwords match existing
				
			}
			else if (msgType == CASE_REGISTER) {
				// TODO save pictures
				String userName = json.getString(JSON_KEY_USERNAME);
				String password = json.getString(JSON_KEY_PASSWORD);
				String email = json.getString(JSON_KEY_EMAIL);
				DbConnection dbConnection = new DbConnection();
				dbConnection.addUser(userName, password, email);
			}
			else if (msgType == CASE_TEXT_MESSAGE) {
				sendClientText(websocket, json);
				
			}
			else if (msgType == CASE_UNREGISTER) {
				// TODO unregister client
			}
			else if (msgType == CASE_EDIT_PROFILE) {
				// TODO edit profile
			}
			else {
				// TODO send error - unknown 
				websocket.send(message);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// TODO: check if message from host is a registration request
		// or a chat message to forward and send it 
		// to the appropriate class or methods
		
		

		// TODO add client data to hashmap
		// check if client exists in client manager, if not add.
		// if client exists in client manager, set isOnline tag	
		
	}

	/**
	 * Called when a new connection is established with a remote host.
	 */
	@Override
	public void onOpen(WebSocket websocket, ClientHandshake handshake) {
		
		System.out.println("Connection established with a client.");
		System.out.println("WebSocket: " + websocket.toString());
		System.out.println("Handshake: " + handshake.toString());
		
	}
	
	
	private void unregisterClient(WebSocket websocket, JSONObject message) {
		// TODO unregister client
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
		WebSocket recipientSocket = getWebSocketByClientId(recipient.getId());
		if (recipient.isOnline() && recipientSocket != null) {
				recipientSocket.send(message.toString());
		}
		else {
			// TODO create a queue/pool of messages to send when clients connect
			
		}		
	}
	
	/**
	 * Search for the websocket associated with a client ID within the connected clients hashmap
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
				// return the websocket that was the key for the client ID value in the hashmap
				return nextSet.getKey(); 
			}
		}
		return null;
	}

	private void registerClient (WebSocket websocket, JSONObject message) {
		// TODO register client
	}
	private void loginClient (WebSocket websocket, JSONObject message) {
		// TODO login client
	}
	private void editClientProfile (WebSocket websocket, JSONObject message) {
		// TODO edit client profile (picture?)
	}
}
