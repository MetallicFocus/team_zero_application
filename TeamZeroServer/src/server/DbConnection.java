package server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A class to connect to and perform queries on the database
 *
 */
public class DbConnection {

	private final String url = "jdbc:postgresql://localhost:5432/testdb";
	private final String user = "postgres";
	private final String password = "123456";

	private static final String COLUMN_USERNAME = "username";
	private static final String COLUMN_EMAIL = "email";
	private static final String COLUMN_ID = "id";
	

	/**
	 * Connect to the PostgreSQL database
	 *
	 * @return a Connection object
	 */
	public Connection connect() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("Connected to the PostgreSQL server successfully.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	public void addUser(String userName, String password, String email) {
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
					System.out.println("Username already exists");
				} else if (existingEmail.equals(email)) {
					System.out.println("Email already exists");
				}
			} else {
				ps = conn.prepareStatement("INSERT INTO USERS (username,password,email) VALUES (?, ?, ?)");
				ps.setString(1, userName);
				ps.setString(2, getMd5(password));
				ps.setString(3, email);
				ps.executeUpdate();
				System.out.println("Added user.");
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteUser(String userName, String password) {
		Connection conn = connect();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("DELETE FROM USERS WHERE username = ? AND password = ?;");
			ps.setString(1, userName);
			ps.setString(2, getMd5(password));
			ps.executeUpdate();
			System.out.println("Deleted user.");
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Authenticates a set of user information. 
	 * Checks the database to see if the username and password match an existing entry.
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
					return client; 
				}
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; 
	}
	
	/**
	 * Gets a list of all users from the database. Useful for updating client contact lists.
	 * @return an arraylist of Client objects
	 */
	public ArrayList<Client> getAllUserInfo(){
		ArrayList<Client> allClients = new ArrayList<Client>();
		Connection conn = this.connect();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS;");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int clientId = rs.getInt(COLUMN_ID);
				String clientEmail = rs.getString(COLUMN_EMAIL);
				String clientUsername = rs.getString(COLUMN_USERNAME);

				
				//if the user is also in the client manager, set them to logged in
				boolean isLoggedIn = ClientManager.getInstance().getClientById(clientId) != null;
				Client client = new Client(clientUsername, clientEmail, clientId, isLoggedIn);
				allClients.add(client);
			}	
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return allClients;	
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
