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

    ArrayList<ClientThread> threads = new ArrayList<ClientThread>();
  
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
                this.datSock = new DatagramSocket(port);
                this.receive = new byte[65535];
                this.dgPack = null;

                System.out.println("=======================================================");
                System.out.println("Server started!");
                System.out.println("Running UDP on Port " + port);

                while(true) {
                    try {
                        this.dgPack = new DatagramPacket(receive, receive.length);
                        this.datSock.receive(this.dgPack);
                        //String ipConnected = this.datSock.getRemoteSocketAddress().toString();
                        //System.out.println(new Timestamp(System.currentTimeMillis()) + " New connection from: " + ipConnected.substring(1) + "\n");
                        System.out.println("Client: " + receive);
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

                    String ipConnected = cSocket.getRemoteSocketAddress().toString();
                    System.out.println(new Timestamp(System.currentTimeMillis()) + " New connection from: " + ipConnected.substring(1) + "\n");

                    synchronized(threads) {
                        threads.add(new ClientThread(cSocket, protocol));
                        Thread t = new Thread(threads.get(threads.size() - 1));
                        t.start();
                    }
                }
            }
        catch(IOException e1) {
            System.out.println("Uh oh! An exception occured");
        }

        }
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
         
      
        public void run() {

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
                        pwt.println(clientMsg);
                        pwt.flush();
                    }
                    else {
                        serverResponse = "Terminating connection...";
                        System.out.println(serverResponse);
                        pwt.println(clientMsg);
                        pwt.flush();
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
        else {
            // setting up socket for UDP communication
            //DatagramSocket dataSock = new DatagramSocket();
        }

        System.out.println("Client disconnected!");
        }
      
   }
}
