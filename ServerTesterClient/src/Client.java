import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.*;
import org.java_websocket.handshake.ServerHandshake;

public class Client extends WebSocketClient {
	
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

		String message = "test message";
		this.send(message);
		
	}

}
