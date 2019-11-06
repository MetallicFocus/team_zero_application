package server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A class to connect to and perform queries on the database
 *
 */
public class DbConnection {

	private final String url = "jdbc:postgresql://ec2-54-228-252-67.eu-west-1.compute.amazonaws.com:5432/dfhffsp1a17jm1";
	private final String user = "sdakjchdnvngqe";
	private final String password = "5d3265328809eecb17f621d43a085c7809c77815bbdae835c5e58acc70ae0413";

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
		DbConnection dbConnection = new DbConnection();
		Connection conn = dbConnection.connect();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM USERS WHERE username = ? OR email = ?;");
			ps.setString(1, userName);
			ps.setString(2, email);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String existingUser = rs.getString("username");
				String existingEmail = rs.getString("email");
				// TODO: Send errors to frontend
				if (existingUser.equals(userName)) {
					System.out.println("Username already exists");
				} else if (existingEmail.equals(email)) {
					System.out.println("Email already exists");
				}
			} else {
				ps = conn.prepareStatement("INSERT INTO USERS (username,email,password) VALUES (?, ?, ?)");
				ps.setString(1, userName);
				ps.setString(2, email);
				ps.setString(3, getMd5(password));
				ps.executeUpdate();
				System.out.println("Added user.");
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteUser(String userName, String password) {
		DbConnection dbConnection = new DbConnection();
		Connection conn = dbConnection.connect();
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
