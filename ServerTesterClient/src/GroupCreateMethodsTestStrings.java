

public class GroupCreateMethodsTestStrings {
	
	private String username1 = "t01";

	private String password1 = "testpassword";

	private String groupName1 = "testGroupName";

	private String groupName3 = "testGroupName3";
	private String groupName2 = "testGroupName2";
	
	private String username2 = "t02";

	private String username3 = "t03";
	
	private String password2 = "testpassword2";	
	
	private String password3 = "testpassword";
	

	public String getClient1GroupCreateMessage() {
		String jsonMsg =
				"{\"type\": \"CREATEGROUP\"" + 
				", \"username\":\"" + username1 + 
				"\", \"groupName\":\"" + groupName1 + 
				"\", \"picture\":null}";
		return jsonMsg;	
		}	
	
	public String getClient2GroupCreateMessage() {
		String jsonMsg =
				"{\"type\": \"CREATEGROUP\"" + 
				", \"username\":\"" + username2 + 
				"\", \"groupName\":\"" + groupName2 + 
				"\", \"picture\":null}";
		return jsonMsg;	
		}	

	public String getClient3GroupCreateMessage() {
		String jsonMsg =
				"{\"type\": \"CREATEGROUP\"" + 
				", \"username\":\"" + username3 + 
				"\", \"groupName\":\"" + groupName3 + 
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

	public String getClient2JoinGroupMessage() {
		String jsonMsg =
				"{\"type\": \"JOINGROUP\"" + 
				", \"username\":\"" + username2 + 
				"\", \"groupName\":\"" + groupName1 + 
				"\"}";
		return jsonMsg;
	}
	
	public String getClient3JoinGroupMessage() {
		String jsonMsg =
				"{\"type\": \"JOINGROUP\"" + 
				", \"username\":\"" + username3 + 
				"\", \"groupName\":\"" + groupName1 + 
				"\"}";
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
	public String getClient3LoginMessage() {
		String jsonMsg = 
				"{\"type\": \"LOGIN\"" + 
				", \"username\":\"" + username3 + 
				"\", \"password\":\"" + password3 + 
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
	public String getClient1ToGroup1TextMessage() {
		String jsonMsg =  
				"{\"type\": \"GROUPTEXT\"" + 
				", \"sender\":\"" + username1 + 
				"\", \"recipient\":\"" + groupName1 + 
				"\", \"message\":\"message from test client 1 to test group 1.\"}";  
		return jsonMsg;
		
	}
	

	public String getGetAllGroupsMessage() {
		String jsonMsg = 
				"{\"type\": \"GETALLGROUPS\"}";
		return jsonMsg;
	}

	public String searchContactsMessage(String search) {
		String jsonMsg = 
				"{\"type\": \"SEARCHGROUPS\"" +
				", \"search\": \"" + search + "\"}";
		return jsonMsg;
	}

	public String getRequestTextsMessage() {
		String jsonMsg = "{\"type\": \"GETCHATHISTORY\"" + 
				", \"myUsername\":\"" + username2 + 
				"\", \"theirUsername\":\"" + username1 +
				"\", \"historyDays\":\"" + 80 
				+ "\"}";
		return jsonMsg;
	}

	public String getClient2ToGroupTextMessage() {
		String jsonMsg =  
				"{\"type\": \"GROUPTEXT\"" + 
				", \"sender\":\"" + username2 + 
				"\", \"recipient\":\"" + groupName1 + 
				"\", \"message\":\"message from test client 2 to test group 1.\"}";  
		return jsonMsg;
		
	}

}
