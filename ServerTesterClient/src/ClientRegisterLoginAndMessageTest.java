

public class ClientRegisterLoginAndMessageTest {

	private String email1 = "testclient01@testclient.com";
	
	private String username1 = "ClientRegisterLoginAndMessageTest01";
	
	private String password1 = "testpassword";
	
	private String email2 = "testclient02@testclient.com";
	
	private String username2 = "ClientRegisterLoginAndMessageTest02";
	
	private String password2 = "testpassword2";
	
	public String getClient1RegisterMessage() {
		String jsonMsg =
				"{\"type\": \"REGISTER\"" + 
				", \"username\":\"" + username1 + 
				"\", \"password\":\"" + password1 + 
				"\", \"email\":\"" + email1 +
				"\", \"picture\":null}";
		return jsonMsg;	
		}	

	public String getClient1UnRegisterMessage() {
		String jsonMsg = 
				"{\"type\": \"UNREGISTER\"" + 
				", \"username\":\"" + username1 + 
				"\", \"password\":\"" + password1 + 
				"\"}";
		return jsonMsg;
	}

	public String getClient2UnRegisterMessage() {
		String jsonMsg =
				"{\"type\": \"UNREGISTER\"" + 
				", \"username\":\"" + username2 + 
				"\", \"password\":\"" + password2 + 
				"\"}";
		return jsonMsg;
	}
	
	public String getClient2RegisterMessage() {
		String jsonMsg =  
				"{\"type\": \"REGISTER\"" + 
				", \"username\":\"" + username2 + 
				"\", \"password\":\"" + password2 + 
				"\", \"email\":\"" + email2 +
				"\", \"picture\":null}";
		return jsonMsg;
		
	}	
	public String getClient1LoginMessage() {
		String jsonMsg = 
				"{\"type\": \"LOGIN\"" + 
				", \"username\":\"" + username1 + 
				"\", \"password\":\"" + password1 + 
				"\"}";
		return jsonMsg;
		
	}

	public String getClient2LoginMessage() {
		String jsonMsg = 
				"{\"type\": \"LOGIN\"" + 
				", \"username\":\"" + username2 + 
				"\", \"password\":\"" + password2 + 
				"\"}";
		return jsonMsg;
		
	}
	public String getClient1To2TextMessage() {
		String jsonMsg =  
				"{\"type\": \"TEXT\"" + 
				", \"sender\":\"" + username1 + 
				"\", \"recipient\":\"" + username2 + 
				"\", \"message\":\"another test message from some test client 1 to test client 2.\"}";  
		return jsonMsg;
		
	}
	
	

	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	public String getUsername1() {
		return username1;
	}

	public void setUsername1(String username1) {
		this.username1 = username1;
	}

	public String getPassword1() {
		return password1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	public String getUsername2() {
		return username2;
	}

	public void setUsername2(String username2) {
		this.username2 = username2;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public String getClient1BadLoginMessage() {
		String jsonMsg = 
				"{\"type\": \"LOGIN\"" + 
				", \"username\":\"" + username1 + 
				"\", \"password\":\"wrongpassword\"}";
		return jsonMsg;
	}

	public String getGetAllContactsMessage() {
		String jsonMsg = 
				"{\"type\": \"GETALLCONTACTS\"}";
		return jsonMsg;
	}

}
