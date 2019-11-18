Once a connection with a client is established, the server can receive string text messages from them. These message are expected to be in JSON format, with one of the
following types:

To register with the server for the first time:
	{
		type: "REGISTER",
		username:,
		password:,
		email:,
		picture:
	}

To send a text message to another client with a known username:
	{
		type: "TEXT",
		sender: "fromUsername",
		recipient: "toUsername",
		message: "message"
	}

To unregister and delete client information from the server:
	{
		type: "UNREGISTER",
		username:,
		password:
	}

For a registered client to login to the platform (to log-out, simply request to close connection)
	{
		type: "LOGIN",
		username:,
		password:
	}

To edit profile (specifically, profile picture) details
	{
		type: "EDIT",
		username:,
		newPicture:
	}
To search for a username
	{
		type: "SEARCHUSER"
		search: "username"
	}
To create a group
	{
		type: "MAKEGROUP"
		members: ["username", "username2, "username3"]
	}
To get all info about the clients' contacts and groups
	{
		type: "CHATINFO"
	}
