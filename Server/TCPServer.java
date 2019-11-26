import java.net.*; 
import java.io.*; 
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.Timestamp;
  
public class TCPServer { 
    
    private Socket cSocket = null; 
    private ServerSocket sSocket = null; 
    private DataInputStream input = null;
    public int clientsConnected = 0;
    ArrayList<ClientThread> threads = new ArrayList<ClientThread>();
  
    // constructor with port 
    public TCPServer(int port) { 
       
         try{
            String serverIP = (Inet4Address.getLocalHost().getHostAddress());
            System.out.println("IP Address: " + serverIP);
            System.out.println("IP Hostname: " + InetAddress.getLocalHost().getHostName());
         } 
         catch(UnknownHostException uhe){
            System.out.println("Exception caught while setting Server IP");
         }
         
         
         try {
            sSocket = new ServerSocket(port);
            System.out.println("=======================================================");
            System.out.println("Server started!");
            System.out.println("Running TCP on Port " + port);
         
            while(true) {
               for (int i = 0; i < threads.size(); i++) {
                  if (threads.get(i).getSocket().isConnected() == false) {
                     clientsConnected--;                     
                  }
               }
            
            
               Socket cSocket = null; /* Client Socket */
            
               try {
                  /* Wait for a connection */
                  cSocket = sSocket.accept();
                  clientsConnected++;      
               }
               catch(IOException e1) {
                  System.out.println("Uh oh! An exception occured when accepting a new client connection");
               }
            
               String ipConnected = cSocket.getRemoteSocketAddress().toString();
               System.out.println(new Timestamp(System.currentTimeMillis()) + " New connection from: " + ipConnected.substring(1) + "\n");
                              
               synchronized(threads) {
               
                  threads.add(new ClientThread(cSocket));
                  Thread t = new Thread(threads.get(clientsConnected - 1));
                  t.start();
               }
            } 
         } 
         catch(IOException e1) {
            System.out.println("Uh oh! An exception occured");
         }
   }
   
   /**
      ClientThread - creates new thread upon socket connection
      of a client.
    */
      
   class ClientThread extends Thread {
      String clientIP;
      Socket cSocket = null;
      String clientMsg;
      String serverResponse;
      
      public ClientThread(Socket clientsSocket) {
         cSocket = clientsSocket;
         clientIP = cSocket.getRemoteSocketAddress().toString();
      }
         
      public Socket getSocket() {
         return cSocket;
      }
         
      
      public void run() {
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
   
         System.out.println("Client disconnected!");
         clientsConnected--;
      }
      
   }
}
