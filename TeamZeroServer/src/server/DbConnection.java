package server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
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

	public boolean addUser(String userName, String password, String email) {
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
				// TODO: Send errors to frontend
				if (existingUser.equals(userName)) {

					LOGGER.log(Level.WARNING, "Username already exists");
					success = false;
				} else if (existingEmail.equals(email)) {

					LOGGER.log(Level.WARNING, "Email already exists");

					success = false;
				}
			} else {
				ps = conn.prepareStatement("INSERT INTO USERS (username,email,password) VALUES (?, ?, ?)");
				ps.setString(1, userName);
				ps.setString(2, email);
				ps.setString(3, getMd5(password));
				ps.executeUpdate();

				LOGGER.log(Level.FINE, "Added user {0}", userName);
				success = true;
			}
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	/**
	 * TODO note currently this will not work if there are any chat messages where this user is referenced.
	 * @param userName
	 * @param password
	 * @return
	 */
	public boolean deleteUser(String userName, String password) {
		Connection conn = connect();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("DELETE FROM USERS WHERE username = ? AND password = ?;");
			ps.setString(1, userName);
			ps.setString(2, getMd5(password));
			ps.executeUpdate();

			LOGGER.log(Level.FINE, "Deleted user {0}", userName);
			ps.close();
			conn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
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
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS WHERE username = ? AND password = ?;");
			ps.setString(1, userName);
			ps.setString(2, getMd5(password));

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
	 */
	public ArrayList<Client> getAllUserInfo() {
		ArrayList<Client> allClients = new ArrayList<Client>();
		Connection conn = this.connect();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS;");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int clientId = rs.getInt(COLUMN_ID);
				String clientEmail = rs.getString(COLUMN_EMAIL);
				String clientUsername = rs.getString(COLUMN_USERNAME);

				// if the user is also in the client manager, set them to logged in
				boolean isLoggedIn = ClientManager.getInstance().getClientById(clientId) != null;
				Client client = new Client(clientUsername, clientEmail, clientId, isLoggedIn);
				allClients.add(client);
			}
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return allClients;
	} 
	
	/**
	 * Gets a list of users from the database that match the query.
	 * 
	 * @return an arraylist of Client objects
	 */
	public ArrayList<Client> getSearchedUsers(String query) {
		ArrayList<Client> searchedClients = new ArrayList<Client>();
		Connection conn = this.connect();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS WHERE lower(username) LIKE ?;");
			
			ps.setString(1, "%" + query.toLowerCase() + "%");
			ResultSet rs = ps.executeQuery();
			LOGGER.log(Level.INFO, "getSearchedUsers prepared statement is:  {0}", ps); //debug
			
			while (rs.next()) {
				int clientId = rs.getInt(COLUMN_ID);
				String clientEmail = rs.getString(COLUMN_EMAIL);
				String clientUsername = rs.getString(COLUMN_USERNAME);

				// if the user is also in the client manager, set them to logged in
				boolean isLoggedIn = ClientManager.getInstance().getClientById(clientId) != null;
				Client client = new Client(clientUsername, clientEmail, clientId, isLoggedIn);
				searchedClients.add(client);
			}
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return searchedClients;
	} 
	

	public void addMessage(String sender, String recipient, String textMessage, String timestamp) {
		Connection conn = connect();
		int chatId = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(
					"SELECT * FROM chats WHERE (user1 = ? AND user2 = ?) OR (user1 = ? AND user2 = ?);");
			
			// TODO do this within same statement to avoid making multiple DB calls
			ps.setInt(1, getUserIDFromUsername(sender));
			ps.setInt(2, getUserIDFromUsername(recipient));
			ps.setInt(3, getUserIDFromUsername(recipient));
			ps.setInt(4, getUserIDFromUsername(sender));
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				chatId = rs.getInt(COLUMN_CHAT_ID);
				ps = conn.prepareStatement(
						"INSERT INTO chat_message (chat_id,sender_id,recipient_id,message_content,timesent) VALUES (?, ?, ?, ?, ?)");
				ps.setInt(1, chatId);
				ps.setInt(2, getUserIDFromUsername(sender));
				ps.setInt(3, getUserIDFromUsername(recipient));
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
				ps1.setInt(1, getUserIDFromUsername(sender));
				ps1.setInt(2, getUserIDFromUsername(recipient));
				ResultSet result = ps1.executeQuery();
				if (result.next()) {
					chatId = result.getInt(1);
					LOGGER.log(Level.FINE, "Adding text message to chats: {0}", textMessage);
					ps1 = conn.prepareStatement(
							"INSERT INTO chat_message (chat_id,sender_id,recipient_id,message_content,timesent) VALUES (?, ?, ?, ?, ?)");
					ps1.setInt(1, chatId);
					ps1.setInt(2, getUserIDFromUsername(sender));
					ps1.setInt(3, getUserIDFromUsername(recipient));
					ps1.setString(4, textMessage);
					Date date = new Date();
					Timestamp ts = new Timestamp(date.getTime());
					ps1.setTimestamp(5, ts);
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
		}
	}

	public int getUserIDFromUsername(String userName) {
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
