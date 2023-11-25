import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
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
	
		try {
			// Connect to the ServerSocket at host:port
			socket = new Socket(host, Integer.valueOf(port));
				
			//IO Object stream
			outObj= new ObjectOutputStream(socket.getOutputStream());
			inObj = new ObjectInputStream(socket.getInputStream());
	
			//try to login
			do {
				message = new Message(MessageType.Login);

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

			// TODO: temporary shared structures
			ArrayList<Message> messagesToSend = new ArrayList<Message>();
			ArrayList<Message> messagesReceived = new ArrayList<Message>();

			ClientReader reader = new ClientReader(inObj, messagesReceived);
			ClientWriter writer = new ClientWriter(outObj, messagesToSend, userID);

			Thread readerThread = new Thread(reader);
			Thread writerThread = new Thread(writer);

			// Start the reader and writer threads
			readerThread.start();
			writerThread.start();

			// Busy wait while the threads are running
			while (true) {
				// Writer thread will die when a logout message was sent
				if (!writerThread.isAlive()) {
					// Stop the reader thread
					readerThread.interrupt();

					// TODO: remove
					System.out.println("Logged out successfully");

					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (outObj != null) {
					outObj.close();
				}

				if (inObj != null) {
					inObj.close();
					socket.close();
					sc.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
    	