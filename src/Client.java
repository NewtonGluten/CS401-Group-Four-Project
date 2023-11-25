import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Client class
class Client {
	
    public static void main(String[] args) throws IOException {
    	String host = "localhost";
    	String port = "1234";
    	Socket socket = null;
		Message message = null;
		ObjectOutputStream outObj = null;
		ObjectInputStream inObj = null;
		
		User currUser = null;
		String userID = null;
		
		Scanner sc = new Scanner(System.in);
		String line = null;
		
    	try {
        // Connect to the ServerSocket at host:port
        socket = new Socket(host, Integer.valueOf(port));
        	
        //IO Object stream
		outObj= new ObjectOutputStream(socket.getOutputStream());
		inObj = new ObjectInputStream(socket.getInputStream());
		
				//try to login
        do {
		message = new Message(MessageType.Login);
		message = new Message(MessageType.Login);
		message.setUserId("someone");
		message.setPassword("password");
        outObj.writeObject(message);
					message = new Message(MessageType.Login);
		message.setUserId("someone");
		message.setPassword("password");
        outObj.writeObject(message);

					System.out.print("Username: ");
					message.setUserId(sc.nextLine());

					System.out.print("Password: ");
					message.setPassword(sc.nextLine());

					outObj.writeObject(message);
        	
        	// Wait for server reply confirmation
    			message = (Message) inObj.readObject();

					if (message.type == MessageType.Login) {
						// If the message contains the User object then login was successful
						currUser = message.getUser();

						if (currUser == null) {
							System.out.println("Login Failed");
						} else {
							userID = currUser.getId();
							System.out.println("Login Success");
						}
					}
        	
        } while (userID == null);
        

        do {
        	
        	//Prompt user
        	System.out.println("\nEnter a line of text");
        	line = sc.nextLine();
        	
        	if (!line.equalsIgnoreCase("logout")) {
        		
            	//Send to server as message
        		message = new Message(MessageType.NewChat);
						message.setUserId(userID);
						message.setRoomId("d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042");
        		
                message.setContents(line);
            	outObj.writeObject(message);
            	
            	//wait to read server response
            	message = (Message) inObj.readObject();
            	
            	System.out.println(message.getContents());    	
            	
        	} else {
        		
        		//Perform Logout Sequence
        		message = new Message(MessageType.Logout);
        		outObj.writeObject(message);
        		
        		//Wait for server response and print message to terminal
            	message = (Message) inObj.readObject();
            	System.out.println(message.getContents());
            	userID = null;
        	}
   
        } while (userID != null);
        
    	}catch (IOException e){
    		e.printStackTrace();
    	}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (outObj != null) {
					outObj.close();
				}
				if (inObj != null) {
					inObj.close();
					socket.close();
					sc.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
}
    	