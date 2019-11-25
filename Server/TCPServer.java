import java.net.*; 
import java.io.*; 
import java.awt.*;
import java.awt.event.*;
import java.util.*;
  
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
            System.out.println("Running TCP on Port " + port);
         } 
         catch(UnknownHostException uhe){
            System.out.println("Exception caught while setting Server IP");
         }
         
         
         try {
            sSocket = new ServerSocket(port);
         
            while(true) {
               for (int b = 0; b < threads.size(); b++) {
                  if (threads.get(b).getSocket().isConnected() == false) {
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
                  System.out.println("Uh oh! An exception");
               }
            
               System.out.println("Client connected!");
               String ipConnected = cSocket.getRemoteSocketAddress().toString();
               System.out.println("Client connected!  IP: "+ ipConnected.substring(1) + "\n");
               
               System.out.println(cSocket);
               
               synchronized(threads) {
               
                  threads.add(new ClientThread(cSocket));
                  
                  Thread t = new Thread(threads.get(clientsConnected - 1));
                  t.start();
                  
               }
            } 
         } 
         catch(IOException e1) {
            System.out.println("Uh oh! An exception");
         }
   }
   
   /**
      ClientThread - creates new thread upon socket connection
      of a client.
    */
      
   class ClientThread extends Thread {
      Socket cSocket = null;
      
      public ClientThread(Socket clientsSocket) {
         cSocket = clientsSocket;
         
      }
         
      public Socket getSocket() {
         return cSocket;
      }
         
      
      public void run() {
         PrintWriter pwt = null;
         Scanner scn = null;
         boolean message_sent = false;
         boolean gameStarted = false;
         
         try {
               pwt = new PrintWriter(new OutputStreamWriter(cSocket.getOutputStream()));
               scn = new Scanner(new InputStreamReader(cSocket.getInputStream()));
         }
         catch (IOException e1) {
            System.out.println("Uh oh! An exception");
         }
         while (getSocket().isConnected()) {
            System.out.println("Socket was successfully connected");
            try{
               sleep(1000);
            }
            catch (InterruptedException e1) {
               System.out.println("Interrupted Exception");
            }
         }
         
         System.out.println("Client disconnected!");
         clientsConnected--;
      }
      
   }
}
