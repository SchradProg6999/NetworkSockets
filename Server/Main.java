import java.util.*;

public class Main {
   
   public static void main(String args[]) {
   
      int serverPort = 23001;
      boolean serverSetupSuccess = false;
      String protocol = "";
      String userInput = "";

      Scanner scanner = new Scanner(System.in);
      System.out.print("Enter protocol for server configuration (TCP or UDP): ");
      userInput = scanner.next();
      
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
      
      new Server(serverPort, protocol);
   }
}

