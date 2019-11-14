import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.*;
import org.java_websocket.handshake.ServerHandshake;

public class Client extends WebSocketClient {
	

	private static final String MESSAGE_TYPE_REGISTER = "REGISTER";
	
	private static final String MESSAGE_TYPE_UNREGISTER = "UNREGISTER";

	private static final String MESSAGE_TYPE_LOGIN = "LOGIN";

	private static final String MESSAGE_TYPE_TEXT = "TEXT";
	
	public static void main(String args[]) throws URISyntaxException {
		Client c = new Client(new URI("ws://localhost:1234"));
		c.connect();
	}

	public Client(URI serverURI) {
		super(serverURI);

		System.out.println("Client constructor with URI: " + serverURI.toString());
		// TODO Auto-generated constructor stub

	}

	@Override
	public void onClose(int arg0, String arg1, boolean arg2) {
		// TODO Auto-generated method stub
		System.out.println("Client onClose: " + arg1);
		
	}

	@Override
	public void onError(Exception arg0) {
		// TODO Auto-generated method stub

		System.out.println("Client onError: " + arg0.getMessage());
		
	}

	@Override
	public void onMessage(String arg0) {
		// TODO Auto-generated method stub

		System.out.println("Client onMessage:" + arg0);
	}

	@Override
	public void onOpen(ServerHandshake arg0) {
		// TODO Auto-generated method stub

		System.out.println("connected with server: " + arg0.toString());
		System.out.println("Sending test message");

		ClientRegisterLoginAndMessageTest test = new ClientRegisterLoginAndMessageTest();
		
		String client1Registers = test.getClient1RegisterMessage();
		this.send(client1Registers);
		
		try {
			this.wait(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String client2Registers = test.getClient2RegisterMessage();
		this.send(client2Registers);

		try {
			this.wait(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String client1LogsIn = test.getClient1LoginMessage();
		this.send(client1LogsIn);

		try {
			this.wait(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String client2LogsIn = test.getClient2LoginMessage();
		this.send(client2LogsIn);

		try {
			this.wait(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String message = test.getClient1To2TextMessage();
		this.send(message);

		try {
			this.wait(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}

}
