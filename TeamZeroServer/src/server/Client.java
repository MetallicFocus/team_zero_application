package server;
/**
 * 
 * Encapsulates a single chat Client
 *
 */
public class Client {
	private String username;
	private String email;
	private int id;
	private boolean isOnline;
	

	// TODO add an image object for profile pics 
	public Client(String username, String email, int id, boolean isOnline) {
		super();
		this.username = username;
		this.email = email;
		this.id = id;
		this.isOnline = isOnline;
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
	public boolean isOnline() {
		return isOnline;
	}
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	

}
