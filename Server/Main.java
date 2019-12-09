import java.util.*;

public class Main {
   
   public static void main(String args[]) {
   
      int serverPort = 23001;
      boolean serverSetupSuccess = false; //Starting ServerSetupSuccess as false or else our code would run with no selection of TCP or UDP
      String protocol = "";
      String userInput = "";

      Scanner scanner = new Scanner(System.in);
      System.out.print("Enter protocol for server configuration (TCP or UDP): ");
      userInput = scanner.next(); //Takes user input and stores it
      
      //Do while loop that will run until serverSetupSuccess = true
      //If neither TCP/UDP is entered, will prompt user to enter either TCP or UDP, will reprompt for TCP/UDP selection
      do {         
         if(userInput.equals("TCP")) {
            serverSetupSuccess = true;
            protocol = userInput;
         }
         else if(userInput.equals("UDP")){
            serverSetupSuccess = true;
            protocol = userInput;
         }
         else {
            System.out.println("Please enter either 'TCP' or 'UDP'...");
            System.out.print("Server Configuration: ");
            userInput = scanner.next();
         }            
      }
      while(serverSetupSuccess != true);
      
      // Server is then established using a call to server from Server.java
      new Server(serverPort, protocol);
   }
}

