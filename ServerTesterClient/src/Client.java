import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.*;
import org.java_websocket.handshake.ServerHandshake;

public class Client extends WebSocketClient {
	

	private static final String MESSAGE_TYPE_REGISTER = "REGISTER";
	
	private static final String MESSAGE_TYPE_UNREGISTER = "UNREGISTER";

	private static final String MESSAGE_TYPE_LOGIN = "LOGIN";

	private static final String MESSAGE_TYPE_TEXT = "TEXT";

	public ClientRegisterLoginAndMessageTest test;
	
	public static void main(String args[]) throws URISyntaxException {

		System.out.println("Scenario 1 - client 1 and 2 register, login, then client 1 sends client 2 a message while both are logged in.\n");
		Client1 c1 = new Client1(new URI("ws://localhost:1234"));
		Client2 c2 = new Client2(new URI("ws://localhost:1234"));
		System.out.println("Client 1 connecting");
		c1.connect();

		
		c2.connect();

		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		
		c1.register();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		c1.login();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		c1.text();
        c1.getAllContacts();
        c1.searchSomeContacts("a");

		c2.register();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		c2.login();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
		c1.text();
		
		c1.getTexts();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		

	   //c1.unregister();
	   //c2.unregister();
		
		

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// scenario 2
		// client 1 sends 2 a message when 2 is not logged in
		
		//System.out.println("\nScenario 2 - client 1 and 2 register, "
		//		+ "client 1 logs in and sends client 2 a message, "
		//		+ "then client 1 logs in and receives message.\n");
		

		//c1.register();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//c1.login();
		
		//c2.register();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		c1.text();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//c2.login();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//c1.unregister();
		//c2.unregister();
		
		

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		// scenario 3
		// invalid login details
		

		
	//	System.out.println("\nScenario 3 - client 1 registers and then tries to"
	//			+ "log in with bad password.\n");
		
		//c1.register();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//c1.badLogin();
		
		//c1.unregister();
		
	}

	public Client(URI serverURI) {
		super(serverURI);
		test = new ClientRegisterLoginAndMessageTest();

	}

	@Override
	public void onClose(int arg0, String arg1, boolean arg2) {
		System.out.println("Client onClose: " + arg1);
		
	}

	@Override
	public void onError(Exception arg0) {

		System.out.println("Client onError: " + arg0.getMessage());
		
	}

	@Override
	public void onMessage(String arg0) {

		System.out.println("Client onMessage:" + arg0);
	}

	@Override
	public void onOpen(ServerHandshake arg0) {

		System.out.println("Client onOpen:" + arg0);
		
	}

	public void getAllContacts() {
		System.out.println("Sending message to getAllContacts...");
		String requestAllContacts = test.getGetAllContactsMessage();
		this.send(requestAllContacts);
	}

	public void searchSomeContacts(String search) {
		System.out.println("Sending message to searchContacts...");
		String requestAllContacts = test.searchContactsMessage(search);
		this.send(requestAllContacts);
	}

}

class Client1 extends Client{

	public Client1(URI serverURI) {
		super(serverURI);
	}
	
	
	public void getTexts() {
		String message = test.getRequestTextsMessage();
		this.send(message);
		
	}


	public void register() {
		System.out.println("Sending message to register client 1...");
		String client1Registers = test.getClient1RegisterMessage();
		this.send(client1Registers);
	}
	
	public void login() {	
		System.out.println("Sending message to login client 1...");
		String client1LogsIn = test.getClient1LoginMessage();
		this.send(client1LogsIn);
	}

	
	public void badLogin() {	
		System.out.println("Sending bad login message for client 1...");
		String client1LogsIn = test.getClient1BadLoginMessage();
		this.send(client1LogsIn);
	}
	
	public void text() {
		System.out.println("Sending message to text client 2 from client 1...");
		String message = test.getClient1To2TextMessage();
		this.send(message);
	}
	
	public void unregister(){
		System.out.println("Sending message to unregister client 1...");
		String message = test.getClient2UnRegisterMessage();
		this.send(message);
	}
	
	@Override
	public void onMessage(String arg0) {

		System.out.println("Client1 onMessage:" + arg0);
	}
	
}



class Client2 extends Client{

	public Client2(URI serverURI) {
		super(serverURI);
	}
	
	public void register() {
		System.out.println("Sending message to register client 2...");
		String client2Registers = test.getClient2RegisterMessage();
		this.send(client2Registers);
	}
	
	public void login() {	
		System.out.println("Sending message to login client 2...");
		String client2LogsIn = test.getClient2LoginMessage();
		this.send(client2LogsIn);
	}
	

	public void unregister(){
		System.out.println("Sending message to unregister client 2...");
		String message = test.getClient1UnRegisterMessage();
		this.send(message);
	}
	
	@Override
	public void onMessage(String arg0) {

		System.out.println("Client2 onMessage:" + arg0);
	}

	
}
