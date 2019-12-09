# NetworkSockets
Our sockets project for the Networking Essentials class.

## How to run?
In order to run our files properly, you're gonna want to first launch up and run our Main.java file. Within here you'll be able to set up the server to either run using UDP or TCP.

- When running the Main.java file youll see a prompt asking for UPD or TCP
- After successfully selecting either TCP or UDP, user will be provided with IP address, along with Port number being used. (These are important because you'll be using these to connect using the NetworkClient.java file in a little bit)

From there you'll want to head over to our NetworkClient.java file and start that up. Using this you'll be able to connect to the server with either a UDP or TCP connection.
- When running this file you'll see a prompt asking for Server IP (Which is provided after running and successfully connecting the server )
- You'll then be asked for a port number for the server (also provided earlier)
- The network client will then ask if you want to establish a UDP connection or TCP, based on that itll run the chosen method for sending information
- After that, you'll be able to freely send messages to the Server and have them send it back to the user.