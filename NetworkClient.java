/**
 * Network Client program
 * Can be used for both TCP and UDP based on user input
 * NSSA 290.01
 * Professor Mason
 */


import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.Date;
import java.text.SimpleDateFormat;


/**
 * Client program for both UDP and TCP connection that allows the user to communicate with the server with timestamps
 **/
public class NetworkClient {
 /**
  * Starts the TCP Connection in the client and allows the user to communicate with the server until it terminates the connection with the input end
  * @param String of the IP address
  * @param int of the port number
  **/
 public static void TCP(String IPaddress, int portNumber) throws Exception, IOException {
  Scanner scan = new Scanner(System.in);
  String input = "";
  String ReceiveFromServer = "";
  SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

  try {
   Date date = new Date();
   System.out.println("Connected to server:" + IPaddress + "\n using TCP+ " + "\n Port Number:" + portNumber + "\n at" + dateFormat.format(date));

   Socket socket = new Socket(IPaddress, portNumber);
   PrintWriter outputToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
   BufferedReader inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));


   do {
    System.out.println("Enter the message to be sent to the server, type end to terminate connection:");
    input = scan.nextLine();
    if (input.equals("end")) {
     break;
    } else {
     System.out.println(input + " sent at:" + dateFormat.format(date));
     outputToServer.println(input);
     outputToServer.flush();
     System.out.print("\n");
     ReceiveFromServer = inputFromServer.readLine();
     System.out.println("Server: " + ReceiveFromServer + "\n");
    }

   }
   while (!input.equals("end"));

   date = new Date();
   System.out.println("Terminating connection at:" + dateFormat.format(date));
   socket.close();
  } catch (Exception e) {
   System.out.println("Exception caught when trying to set up socket for client");
  }
 }
 /**
  * Starts the UDP Connection in the client and allows the user to communicate with the server until it terminates the connection with the input end
  * @param String of the IP address
  * @param int of the port number
  **/
 public static void UDP(String IPaddress, int portNumber) throws Exception, IOException {
  Scanner scan = new Scanner(System.in);
  DatagramSocket socket = new DatagramSocket();
  String input = "";
  String loop = "";

  Date date = new Date();
  SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
  System.out.println("Connected to server:" + IPaddress + "\n using UDP " + "\n Port Number:" + portNumber + "\n at " + dateFormat.format(date));

  while (loop.equals("end") == false) {
   System.out.println("Enter the message to be sent to the server, type end to terminate connection:");
   loop = scan.nextLine();
   input = loop;
   byte[] size = input.getBytes();

   System.out.println(input + " sent at: " + dateFormat.format(date));
   input = input + " sent at:" + dateFormat.format(date);
   InetAddress server = InetAddress.getByName(IPaddress);
   DatagramPacket request = new DatagramPacket(size, size.length, server, portNumber);
   socket.send(request);
   request = new DatagramPacket(size,size.length);
   socket.receive(request);
   String serverEcho = new String( request.getData(), 0 , request.getLength());
   
   System.out.println("Message from server: "+serverEcho);
   

  }
  System.out.println("Terminating connection at:" + dateFormat.format(date));
  socket.close();


 }

 public static void main(String args[]) throws Exception, IOException {
  String IP;
  int port;
  String method;

  Scanner keyboard = new Scanner(System.in);

  System.out.print("Enter the IP address of the server:");
  IP = keyboard.next();
  System.out.print("Enter the Port Number of the server:");
  port = keyboard.nextInt();
  System.out.print("Enter TCP/UDP:");
  method = keyboard.next();

  if (method.equals("TCP") == true) {
   TCP(IP, port);
  }
  if (method.equals("UDP") == true) {
   UDP(IP, port);
  }

 }

}
