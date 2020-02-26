import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class GroupTest extends WebSocketClient{
	
	GroupCreateMethodsTestStrings groupTester;

	public GroupTest(URI serverURI) {
		super(serverURI);
		// TODO Auto-generated constructor stub
	}
	


	@Override
	public void onClose(int arg0, String arg1, boolean arg2) {
		System.out.println("GroupTest onClose: " + arg1);
		
	}

	@Override
	public void onError(Exception arg0) {

		System.out.println("GroupTest onError: " + arg0.getMessage());
		
	}

	@Override
	public void onMessage(String arg0) {

		System.out.println("GroupTest onMessage:" + arg0);
	}

	@Override
	public void onOpen(ServerHandshake arg0) {

		System.out.println("GroupTest onOpen:" + arg0);
		
	}
	
	public void runTest() {
		groupTester = new GroupCreateMethodsTestStrings();
		
		this.send(groupTester.getClient1LoginMessage());

		this.send(groupTester.getClient2LoginMessage());
		this.send(groupTester.getClient3LoginMessage());
		
		this.send(groupTester.getClient1GroupCreateMessage());
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.send(groupTester.getClient2JoinGroupMessage());

		this.send(groupTester.getClient2GroupCreateMessage());

		this.send(groupTester.getGetAllGroupsMessage());

		this.send(groupTester.getClient3GroupCreateMessage());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		

		this.send(groupTester.getGetAllGroupsMessage());
		this.send(groupTester.searchGroupsMessage("3"));
		

		
		
		
	}
	
	public static void main(String args[]) throws URISyntaxException {

		System.out.println("Group Test start. \n");
		GroupTest test = new GroupTest(new URI("ws://localhost:1234"));
		test.connect();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		test.runTest();
	}

}
