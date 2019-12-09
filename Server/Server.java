/**
 * Server program
 * Can handle both TCP and UDP clients
 * NSSA 290.01
 * Professor Mason
 */
import java.net.*; 
import java.io.*; 
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.Timestamp;
  
public class Server {
    
    private Socket cSocket = null; 
    private ServerSocket sSocket = null; 
    private DataInputStream input = null;
    private String protocol;
    private DatagramSocket datSock;
    private byte[] receive;
    private DatagramPacket dgPack;

    ArrayList<ClientThread> threads = new ArrayList<ClientThread>(); //Needed to keep track of threads for TCP protocol
  
    // constructor with port 
    public Server(int port, String protocol) {
         this.protocol = protocol;

        try{
            String serverIP = (Inet4Address.getLocalHost().getHostAddress());
            System.out.println("IP Address: " + serverIP);
            System.out.println("IP Hostname: " + InetAddress.getLocalHost().getHostName());
        }
        catch(UnknownHostException uhe){
            System.out.println("Exception caught while setting Server IP");
        }

        if(this.protocol.equals("UDP")) {
            try {
                datSock = new DatagramSocket(port); // Opening socket for communication via UDP
                dgPack = null;

                System.out.println("=======================================================");
                System.out.println("Server started!");
                System.out.println("Running UDP on Port " + port);

                while(true) {
                    receive = new byte[63000]; //Byte Array needed for the message received and sent

                    try {
                        dgPack = new DatagramPacket(receive, receive.length); //Setting up the DatagramPacket to receive from Client
                        datSock.receive(dgPack); 
                        
                        String ipConnected = dgPack.getAddress().toString(); //Retrieving IP address from DatagramPacket that was received
                        System.out.println(new Timestamp(System.currentTimeMillis()) + " New connection from: " + ipConnected.substring(1) + "\n");
                        
                        System.out.println("Sending to client: " + " " + ipConnected.substring(1) + " " + new Timestamp(System.currentTimeMillis()) + " " + stringBuilder(receive));
                        System.out.println("PORT: " + dgPack.getPort());
                        InetAddress clientIP = dgPack.getAddress();
                        
                        int portNum = dgPack.getPort();
                        DatagramPacket response = new DatagramPacket(receive, receive.length, clientIP, portNum); //Setting up the DatagramPacket to send message back to client
                        datSock.send(response); //Send the message back to client
                        
                        receive = null; //To flush out the message to keep the buffer clean
                        
                    }
                    catch(IOException ioe) {
                        System.out.println("Error with datagram packet: " + ioe);
                    }

                }
            }
            catch(SocketException se) {
                System.out.println("Failure in creating Datagram Socket: " + se);
            }
        }
        else {
            try {
                sSocket = new ServerSocket(port);
                System.out.println("=======================================================");
                System.out.println("Server started!");
                System.out.println("Running TCP on Port " + port);

                while(true) {

                    Socket cSocket = null; /* Client Socket */

                    try {
                        /* Wait for a connection */
                        cSocket = sSocket.accept();
                    }
                    catch(IOException e1) {
                        System.out.println("Uh oh! An exception occured when accepting a new client connection");
                    }

                    String ipConnected = cSocket.getRemoteSocketAddress().toString(); //Getting the IP address of the client that connected
                    System.out.println(new Timestamp(System.currentTimeMillis()) + " New connection from: " + ipConnected.substring(1) + "\n");

                    synchronized(threads) { //Allows one thread to be made at a time and added to the thread arraylist then gets thread started .
                        threads.add(new ClientThread(cSocket, protocol));
                        Thread t = new Thread(threads.get(threads.size() - 1)); //Getting last thread instance that was added to the arraylist to ensure it is the most recent connection
                        t.start();
                    }
                }
            }
        catch(IOException e1) {
            System.out.println("Uh oh! An exception occured");
        }

        }
   }
   
   
   // helper funciton to build the string from byte array recieved over the network
   // Credit: https://www.geeksforgeeks.org/working-udp-datagramsockets-java/
   public StringBuilder stringBuilder(byte[] byteArr) {
     
      if (byteArr == null) {
         return null;
      }
   
      StringBuilder sb = new StringBuilder(); 
      int i = 0; 
   
      while (byteArr[i] != 0) { 
         sb.append((char) byteArr[i]); 
         i++; 
      } 
      return sb; 
   } 
   
   /**
      ClientThreadTCP - creates new thread upon socket connection
      of a client.
    */
      
   class ClientThread extends Thread {
        String clientIP;
        Socket cSocket = null;
        String clientMsg;
        String serverResponse;
        String protocol;
        DatagramPacket dgPack = null;

        public ClientThread(Socket clientsSocket, String protocol) {
            this.protocol = protocol;
            cSocket = clientsSocket;
            clientIP = cSocket.getRemoteSocketAddress().toString();
        }
         
        public Socket getSocket() {
         return cSocket;
        }
         
      
        public void run() { //Runs TCP connection

        if(this.protocol.equals("TCP")) {
            PrintWriter pwt = null;

            try {
                pwt = new PrintWriter(new OutputStreamWriter(cSocket.getOutputStream()));
                InputStream inputStream = cSocket.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(inputStreamReader);

                Timestamp ts;
                while (getSocket().isConnected()) {
                    clientMsg = br.readLine();

                    if(clientMsg != null) {
                        serverResponse = "Sending to client: " + clientIP.substring(1) + " " + new Timestamp(System.currentTimeMillis()) + " " + clientMsg;
                        System.out.println(serverResponse);
                        pwt.println(clientMsg); //Adding client message to buffer
                        pwt.flush(); //flushes the buffer to send the message
                    }
                    else {
                        serverResponse = "Terminating connection...";
                        System.out.println(serverResponse);
                        pwt.println(clientMsg); //Adding client message to buffer
                        pwt.flush(); //flushes the buffer to send the message
                        break;
                    }

                    try{
                        sleep(1000);
                    }
                    catch (InterruptedException e1) {
                        System.out.println("Interrupted Exception while trying to reader client data");
                    }
                }
            }
            catch (IOException ioe) {
                System.out.println("IOException during creation of input/output components in client thread class");
                System.out.println(ioe);
            }
        }

        System.out.println("Client disconnected!");
        }
      
   }
}
