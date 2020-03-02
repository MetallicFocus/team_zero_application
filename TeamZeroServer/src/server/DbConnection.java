package server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;

/**
 * A class to connect to and perform queries on the database
 *
 */
public class DbConnection {

	private static final Logger LOGGER = Logger.getLogger("ServerLog");

	private final String url = "jdbc:postgresql://ec2-54-228-252-67.eu-west-1.compute.amazonaws.com:5432/dfhffsp1a17jm1";
	private final String user = "sdakjchdnvngqe";
	private final String password = "5d3265328809eecb17f621d43a085c7809c77815bbdae835c5e58acc70ae0413";

	private static final String COLUMN_USERNAME = "username";
	private static final String COLUMN_EMAIL = "email";
	private static final String COLUMN_ID = "user_id";
	private static final String COLUMN_CHAT_ID = "chat_id";
	private static final String COLUMN_PUBLIC_KEY = "public_key";
	private static final String COLUMN_GROUPNAME = "group_name";
	private static final String COLUMN_GROUP_ID = "group_id";
	private static final String COLUMN_SENDER_ID = "sender_id";
	private static final String COLUMN_RECIPIENT_ID = "recipient_id";
	private static final int MAX_NUMBER_OF_MEMBERS = 10;

	private static final String COLUMN_TIMESENT = "timesent";

	private static final String COLUMN_MESSAGE_CONTENT = "message_content";

	/**
	 * Connect to the PostgreSQL database
	 *
	 * @return a Connection object
	 */
	public Connection connect() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
			LOGGER.log(Level.FINE, "Connected to the PostgreSQL server successfully.");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Could not connect to PostgreSQL server - {0}", e.getMessage());

		}
		return conn;
	}

	public boolean addUser(String userName, String password, String email, String publicKey) throws SQLException {
		boolean success = true;
		Connection conn = connect();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM USERS WHERE username = ? OR email = ?;");
			ps.setString(1, userName);
			ps.setString(2, email);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String existingUser = rs.getString(COLUMN_USERNAME);
				String existingEmail = rs.getString(COLUMN_EMAIL);
				if (existingUser.equals(userName)) {

					LOGGER.log(Level.WARNING, "Username already exists");
					success = false;
					throw new SQLException("Username already exists");
				} else if (existingEmail.equals(email)) {

					LOGGER.log(Level.WARNING, "Email already exists");
					success = false;
					throw new SQLException("Email already exists");
				}
			} else {
				ps = conn.prepareStatement("INSERT INTO USERS (username,email,password, public_key,unregistered) VALUES (?, ?, ?, ?, ?)");
				ps.setString(1, userName);
				ps.setString(2, email);
				ps.setString(3, getMd5(password));
				ps.setString(4, publicKey);
				ps.setBoolean(5, false);
				ps.executeUpdate();

				LOGGER.log(Level.FINE, "Added user {0}", userName);
				success = true;
			}
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			success = false; // probably never gets returned due to throw
			throw e;
		}
		return success;
	}

	/**
	 * @param userName
	 * @param password
	 * @return
	 * @throws SQLException 
	 */
	public boolean deleteUser(String userName, String password) throws SQLException {
		Connection conn = connect();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE USERS SET unregistered = true WHERE username = ? AND password = ?;");
			ps.setString(1, userName);
			ps.setString(2, getMd5(password));
			ps.executeUpdate();

			LOGGER.log(Level.FINE, "Deleted user {0}", userName);
			ps.close();
			conn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Authenticates a set of user information. Checks the database to see if the
	 * username and password match an existing entry.
	 * 
	 * @param userName clients username
	 * @param passWord clients password
	 * @return the Client object if authenticated, otherwise null
	 */
	public Client authenticateUser(String userName, String password) {
		Connection conn = this.connect();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS WHERE username = ? AND password = ? AND unregistered = ?;");
			ps.setString(1, userName);
			ps.setString(2, getMd5(password));
			ps.setBoolean(3,false);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				String authenticatedUser = rs.getString(COLUMN_USERNAME);
				if (authenticatedUser.equals(userName)) {
					// user is authenticated
					int clientId = rs.getInt(COLUMN_ID);
					String clientEmail = rs.getString(COLUMN_EMAIL);
					Client client = new Client(userName, clientEmail, clientId, true);
					conn.close();
					return client;
				}
			}
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets a list of all users from the database. Useful for updating client
	 * contact lists.
	 * 
	 * @return an arraylist of Client objects
	 * @throws SQLException 
	 */
	public ArrayList<Client> getAllUserInfo() throws SQLException {
		ArrayList<Client> allClients = new ArrayList<Client>();
		Connection conn = this.connect();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS WHERE unregistered = false;");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int clientId = rs.getInt(COLUMN_ID);
				String clientEmail = rs.getString(COLUMN_EMAIL);
				String clientUsername = rs.getString(COLUMN_USERNAME);
				String clientPublicKey = rs.getString(COLUMN_PUBLIC_KEY);

				// if the user is also in the client manager, set them to logged in
				boolean isLoggedIn = ClientManager.getInstance().getClientById(clientId) != null;
				Client client = new Client(clientUsername, clientEmail, clientId, clientPublicKey, isLoggedIn);
				allClients.add(client);
			}
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return allClients;
	} 
	
	/**
	 * Gets a list of users from the database that match the query.
	 * 
	 * @return an arraylist of Client objects
	 * @throws SQLException 
	 */
	public ArrayList<Client> getSearchedUsers(String query) throws SQLException {
		ArrayList<Client> searchedClients = new ArrayList<Client>();
		Connection conn = this.connect();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS WHERE lower(username) LIKE ? AND unregistered = false;");
			
			ps.setString(1, "%" + query.toLowerCase() + "%");
			ResultSet rs = ps.executeQuery();
			LOGGER.log(Level.INFO, "getSearchedUsers prepared statement is:  {0}", ps); //debug
			
			while (rs.next()) {
				int clientId = rs.getInt(COLUMN_ID);
				String clientEmail = rs.getString(COLUMN_EMAIL);
				String clientUsername = rs.getString(COLUMN_USERNAME);
				String clientPublicKey = rs.getString(COLUMN_PUBLIC_KEY);

				// if the user is also in the client manager, set them to logged in
				boolean isLoggedIn = ClientManager.getInstance().getClientById(clientId) != null;
				Client client = new Client(clientUsername, clientEmail, clientId, clientPublicKey, isLoggedIn);
				searchedClients.add(client);
			}
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return searchedClients;
	} 
	

	/**
	 * returns the public encryption key of the client with the given username
	 * @param userName
	 * @return encoded string of public keyzsz
	 */
	public String getPublicKey(String userName) throws SQLException{
		String publicKey = "";
		Connection conn = this.connect();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS WHERE username=?;");
			
			ps.setString(1, userName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				publicKey = rs.getString(COLUMN_PUBLIC_KEY);
			}
			else {
				ps.close();
				conn.close();
				throw new SQLException("No user found with given username.");
			}
			ps.close();
			conn.close();
		} catch (SQLException e) {
			conn.close();
			e.printStackTrace();
			throw e;
		}
		return publicKey;
	}
	

	/**
	 * 
	 * @param thisUserName username of the client requesting the chat history
	 * @param otherUserName username of the other user in the chat
	 * @param daysOfHistory how many days of chat history to retrieve
	 * @return an array list of chatMessage objects
	 * @throws SQLException 
	 */
	public ArrayList<ChatMessage> getMessageHistory(String thisUserName, String otherUserName, int daysOfHistory) throws SQLException {
		ArrayList<ChatMessage> chatHistory = new ArrayList<ChatMessage>();
		int thisUserId = getUserIDFromUsername(thisUserName);
		int otherUserId = getUserIDFromUsername(otherUserName);
		Connection conn = this.connect();
		int chatId = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(
					"SELECT * FROM chats WHERE (user1 = ? AND user2 = ?) OR (user1 = ? AND user2 = ?);");
			ps.setInt(1, thisUserId);
			ps.setInt(2, otherUserId);
			ps.setInt(3, otherUserId);
			ps.setInt(4, thisUserId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				chatId = rs.getInt(COLUMN_CHAT_ID);
				
				// determine history date constraints	
			    Date now = Calendar.getInstance().getTime();
			    Calendar historyLimit = Calendar.getInstance();
			    historyLimit.setTime(now);
			    historyLimit.add(Calendar.DAY_OF_YEAR, -(daysOfHistory));
			    String nowStr = ServerMain.DATE_FORMAT.format(now);
			    String limitStr = ServerMain.DATE_FORMAT.format(historyLimit.getTime());
			    
				ps = conn.prepareStatement(
						"SELECT chat_message.timesent, chat_message.message_content, u1.username AS sender, u2.username AS recipient"
						+ " FROM chat_message "
						+ "INNER JOIN users AS u1 ON (u1.user_id=chat_message.sender_id) "
						+ "INNER JOIN users AS u2 ON (u2.user_id=chat_message.recipient_id)"
						+ " WHERE chat_id = ? AND timesent between ? AND ? ORDER BY timesent ASC;");
				ps.setInt(1, chatId);
				ps.setTimestamp(2, Timestamp.valueOf(limitStr));
				ps.setTimestamp(3, Timestamp.valueOf(nowStr));
				rs = ps.executeQuery();

				while (rs.next()) {
					String timestamp = ServerMain.DATE_FORMAT.format(rs.getTimestamp(COLUMN_TIMESENT));
					String message = rs.getString(COLUMN_MESSAGE_CONTENT);
					String sender = rs.getString("sender");
					String recipient = rs.getString("recipient");
					ChatMessage chatMessage = new ChatMessage(sender, recipient, message, timestamp);
					chatHistory.add(chatMessage);
				}
				ps.close();
				conn.close();
			} 
		}
		catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Could not retrieve messages got error: {0}", e.getMessage()); //debug
				throw e;
		}
		return chatHistory;
	}
	
	/**
	 * Adds a new group (chat) to the database
	 * @param groupName
	 * @param creatingUser - the username of the user that requested the group be created
	 * @return true if the operation is successful
	 * @throws SQLException if there is any issue adding the group to the database
	 */
	public boolean addNewGroup(String groupName, String creatingUser) throws SQLException { //TODO group avatar
		boolean success = true;
		Connection conn = connect();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM groups WHERE group_name = ?;");
			ps.setString(1, groupName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String existingGroupname = rs.getString(COLUMN_GROUPNAME);
				if (existingGroupname.equals(groupName)) {
					LOGGER.log(Level.WARNING, "Groupname already exists");
					success = false;
					throw new SQLException("Groupname already exists");
				}
			} else {
				PreparedStatement ps1 = conn.prepareStatement("INSERT INTO GROUPS (group_name,number_of_members) VALUES (?, ?) RETURNING group_id");
				ps1.setString(1, groupName);
				ps1.setInt(2, MAX_NUMBER_OF_MEMBERS);
				ResultSet result = ps1.executeQuery();

				 LOGGER.log(Level.FINE, "Created new group {0}", groupName);
				
				 // get the newly created group Id
				int groupId = 0;
				if (result.next()) {
					 groupId = result.getInt(1);
				
				// insert the userId and groupId into user_groups table as well
				// this user is also a member of the group they created
					 LOGGER.log(Level.FINE, "Adding group creator {0} to group", creatingUser);
					 ps1 = conn.prepareStatement("INSERT INTO user_groups (user_id,group_id,left_group) VALUES (?, ?,?)");
					 ps1.setInt(1, getUserIDFromUsername(creatingUser));
					 ps1.setInt(2, groupId);
					 ps1.setBoolean(3, false);
					 ps1.executeUpdate();
					 LOGGER.log(Level.FINE, "Added group {0}", groupName);
					 success = true;
					 ps1.close();
				}
			}
			
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			success = false; // probably never gets returned due to throw
			throw e;
		}
		return success;
	}
	
	/**
	 * Gets all the groups that exists in the database and returns them as an array list of Group objects
	 * @return array list of group objects containing the group ID, group name and a list of group member usernames
	 * @throws SQLException
	 */
	public ArrayList<Group> getAllGroups() throws SQLException{
		ArrayList<Group> allGroups = new ArrayList<Group>();
		Connection conn = this.connect();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT g.group_id as group_id, g.group_name as group_name, u.members"
					+ " FROM groups g, LATERAL ( SELECT ARRAY ( "
					+ "SELECT u.username "
					+ "FROM users u "
					+ "JOIN user_groups ug ON ug.user_id=u.user_id "
					+ "WHERE ug.group_id=g.group_id AND u.unregistered=false) AS members ) u;");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int groupId = rs.getInt(COLUMN_GROUP_ID);
				String groupName = rs.getString(COLUMN_GROUPNAME);
				String[] groupMembers = (String[]) rs.getArray("members").getArray();
				Group group = new Group(groupId, groupName, groupMembers);
				allGroups.add(group);
			}
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		LOGGER.log(Level.FINE, "Getting all groups: {0}", allGroups);
		return allGroups;
	}
	
	
	public ArrayList<Group> getSearchedGroups(String search) throws SQLException{
		ArrayList<Group> searchedGroups = new ArrayList<Group>();
		Connection conn = this.connect();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT g.group_id as group_id, g.group_name as group_name, u.members"
					+ " FROM groups g, LATERAL ( SELECT ARRAY ( "
					+ "SELECT u.username "
					+ "FROM users u "
					+ "JOIN user_groups ug ON ug.user_id=u.user_id "
					+ "WHERE ug.group_id=g.group_id AND u.unregistered=false) AS members ) u WHERE lower(group_name) LIKE ?;");
			
			ps.setString(1, "%" + search.toLowerCase() + "%");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int groupId = rs.getInt(COLUMN_GROUP_ID);
				String groupName = rs.getString(COLUMN_GROUPNAME);
				String[] groupMembers = (String[]) rs.getArray("members").getArray();
				Group group = new Group(groupId, groupName, groupMembers);
				searchedGroups.add(group);
			}
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return searchedGroups;
	}
	
	
	/**
	 * Adds a group text message to the database
	 * @param sender the user sending the message
	 * @param groupName the group receiving the message
	 * @param message the contents of the text message 
	 * @param timestamp the time the user sent the message (as received by the server)
	 * @return An arraylist of client usernames that the message needs to be sent to
	 * @throws SQLException
	 */
	public ArrayList<String> addGroupMessage(String sender, String groupName, String message, String timestamp) throws SQLException {
		ArrayList<String> groupMembers = new ArrayList<String>();
		Connection conn = connect();
		try {
			
			// check that the sender is a member of the group
			PreparedStatement ps = conn.prepareStatement(
					"SELECT * FROM user_groups WHERE group_id = ? AND user_id = ? AND left_group = ?");

			int senderId = getUserIDFromUsername(sender);
			int groupId = getGroupIDFromGroupName(groupName);
			
			ps.setInt(1, groupId);
			ps.setInt(2, senderId);
			ps.setBoolean(3, false);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				
				ps = conn.prepareStatement(
						"INSERT INTO group_message (sender_id,group_id,timesent,message_content) VALUES (?, ?, ?, ?)");
				ps.setInt(1, senderId);
				ps.setInt(2, groupId);
				ps.setTimestamp(3, Timestamp.valueOf(timestamp));
				ps.setString(4, message);
				ps.executeUpdate();
				
				// Get all clients in the group except for the client that sent the message
				ps = conn.prepareStatement("SELECT username from users LEFT JOIN user_groups on users.user_id = user_groups.user_id WHERE group_id = ?;");
				ps.setInt(1, groupId);
				rs = ps.executeQuery();
				while(rs.next()) {
					String userName = rs.getString(COLUMN_USERNAME);
					if (userName != sender) {
						groupMembers.add(userName);
					}
				}
				
				ps.close();
			} else {
				// a group with this user does not exist
				// clean up and throw exception so the message can get to the user.

				ps.close();
				conn.close();
				throw new SQLException("No such group with member " + sender + " in it.");
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return groupMembers;
	}
	
	// TODO
	public boolean removeGroupMember(String groupName, String username) throws SQLException{
			return false;
	}
	
	/**
	 * Add a user to a group (chat)
	 * @param groupName - name of group to add the user to
	 * @param username
	 * @return true if the operation is successful
	 * @throws SQLException 
	 */
	public boolean addGroupMember(String groupName, String username) throws SQLException {
		boolean success = true;
		Connection conn = connect();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM groups WHERE group_name = ?;");
			ps.setString(1, groupName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int groupId = rs.getInt(COLUMN_GROUP_ID);
				
				// check if user does not already exist in the group.
				int userId = getUserIDFromUsername(username);
				ps = conn.prepareStatement("SELECT * FROM user_groups WHERE group_id = ? AND user_id = ?;");
				ps.setInt(1, groupId);
				ps.setInt(2, userId);
				rs = ps.executeQuery();
				
				if (!rs.next()) {
					// TODO check number of users with group_id is not more than MAX_NUMBER_OF_MEMBERS
					//add user to user groups table
					ps = conn.prepareStatement("INSERT INTO user_groups (user_id,group_id,left_group) VALUES (?, ?,?)");
					ps.setInt(1, getUserIDFromUsername(username));
					ps.setInt(2, groupId);
					ps.setBoolean(3, false);

					ps.executeUpdate();
					LOGGER.log(Level.FINE, "Added user to group {0}", groupName);
					success = true;
				}
				else {
					//user already exists in this group
					success = false;
				}
			} else {
				// groupName does not exist
				throw new SQLException("Group name does not exist.");		
			}
			
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			success = false; // probably never gets returned due to throw
			throw e;
		}
		return success;
	}

	public void addMessage(String sender, String recipient, String textMessage, String timestamp) throws SQLException {
		Connection conn = connect();
		int chatId = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(
					"SELECT * FROM chats WHERE (user1 = ? AND user2 = ?) OR (user1 = ? AND user2 = ?);");

			int senderId = getUserIDFromUsername(sender);
			int recipientId = getUserIDFromUsername(recipient);
			
			ps.setInt(1, senderId);
			ps.setInt(2, recipientId);
			ps.setInt(3, recipientId);
			ps.setInt(4, senderId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				chatId = rs.getInt(COLUMN_CHAT_ID);
				ps = conn.prepareStatement(
						"INSERT INTO chat_message (chat_id,sender_id,recipient_id,message_content,timesent) VALUES (?, ?, ?, ?, ?)");
				ps.setInt(1, chatId);
				ps.setInt(2, senderId);
				ps.setInt(3, recipientId);
				ps.setString(4, textMessage);
				ps.setTimestamp(5, Timestamp.valueOf(timestamp));
				Date date = new Date();
				Timestamp ts = new Timestamp(date.getTime());
				ps.setTimestamp(5, ts);
				ps.executeUpdate();
				ps.close();
			} else {
				PreparedStatement ps1 = conn
						.prepareStatement("INSERT INTO chats (user1,user2) VALUES (?, ?) RETURNING chat_id");
				ps1.setInt(1, senderId);
				ps1.setInt(2, recipientId);
				ResultSet result = ps1.executeQuery();
				if (result.next()) {
					chatId = result.getInt(1);
					LOGGER.log(Level.FINE, "Adding text message to chats: {0}", textMessage);
					ps1 = conn.prepareStatement(
							"INSERT INTO chat_message (chat_id,sender_id,recipient_id,message_content,timesent) VALUES (?, ?, ?, ?, ?)");
					ps1.setInt(1, chatId);
					ps1.setInt(2, senderId);
					ps1.setInt(3, recipientId);
					ps1.setString(4, textMessage);
					ps1.setTimestamp(5, Timestamp.valueOf(timestamp));
					ps1.executeUpdate();
					ps1.close();
				} else {
					LOGGER.log(Level.WARNING, "Could not add text message to chats in DB: {0}", textMessage);
				}
				ps.close();

			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public int getUserIDFromUsername(String userName) throws SQLException {
		Connection conn = this.connect();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT user_id FROM USERS WHERE username = ?;");
			ps.setString(1, userName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int clientId = rs.getInt(COLUMN_ID);
				ps.close();
				conn.close();
				return clientId;
			}
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return 0;
	}
	
	public int getGroupIDFromGroupName(String groupName) throws SQLException {
		Connection conn = this.connect();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT group_id FROM groups WHERE group_name = ?;");
			ps.setString(1, groupName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int groupId = rs.getInt(COLUMN_GROUP_ID);
				ps.close();
				conn.close();
				return groupId;
			}
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	private String getMd5(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger no = new BigInteger(1, messageDigest);
			String hashtext = no.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}


}
