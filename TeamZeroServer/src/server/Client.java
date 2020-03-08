package server;
/**
 * 
 * Encapsulates a single online chat Client
 *
 */
public class Client {
	private String username;
	private String email;
	private int id;
	private boolean isLoggedIn;
	private String publicKey;
	
	public Client(String username, String email, int id, boolean isLoggedIn) {
		this.username = username;
		this.email = email;
		this.id = id;
		this.isLoggedIn = isLoggedIn;
	}
	
	public Client(String username, String email, int id, String publicKey, boolean isLoggedIn) {
		this.username = username;
		this.email = email;
		this.id = id;
		this.isLoggedIn = isLoggedIn;
		this.setPublicKey(publicKey);
	}
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}


	public boolean isLoggedIn() {
		return isLoggedIn;
	}


	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

}
