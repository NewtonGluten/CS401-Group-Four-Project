import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

// Client class
class Client {
	private static User currUser = null;
	private static List<Room> rooms = null;
	private static List<User> users = null;
	public static void main(String[] args) throws IOException {
		String host = args[0];
		String port = "4014";
		Socket socket = null;
		ObjectOutputStream outObj = null;
		ObjectInputStream inObj = null;
	
		try {
			// Connect to the ServerSocket at host:port
			socket = new Socket(host, Integer.valueOf(port));
				
			//IO Object stream
			outObj= new ObjectOutputStream(socket.getOutputStream());
			inObj = new ObjectInputStream(socket.getInputStream());
	
			doLoginFlow(inObj, outObj);

			ArrayList<Message> outMsgs = new ArrayList<Message>();
			ArrayList<Message> inMsgs = new ArrayList<Message>();

			ClientGUI gui = new ClientGUI(inMsgs, outMsgs, rooms, users, currUser.getId());
			ClientReader reader = new ClientReader(inObj, inMsgs, rooms);
			ClientWriter writer = new ClientWriter(outObj, outMsgs);

			Thread guiThread = new Thread(gui);
			Thread readerThread = new Thread(reader);
			Thread writerThread = new Thread(writer);

			// Start the threads
			guiThread.start();
			readerThread.start();
			writerThread.start();

			// Busy wait while the threads are running
			while (true) {
				// Writer thread will die when a logout message was sent
				if (!writerThread.isAlive()) {
					// Stop the other threads
					guiThread.interrupt();
					readerThread.interrupt();

					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (outObj != null) {
					outObj.close();
				}

				if (inObj != null) {
					inObj.close();
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void doLoginFlow(ObjectInputStream inObj, ObjectOutputStream outObj) {
		try {
			while (currUser == null) {

				JPanel panel = new JPanel();
				JTextField userId = new JTextField(20);
				JTextField password = new JTextField(20);
	
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				panel.add(new JLabel("User ID"));
				panel.add(userId);
				panel.add(new JLabel("Password"));
				panel.add(password);
	
				int result = JOptionPane.showConfirmDialog(
					null,
					panel,
					"Login",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE
				);
	
				if (result == JOptionPane.OK_OPTION
					&& userId.getText().length() > 0
					&& password.getText().length() > 0
				) {
					Message msg = new Message(MessageType.Login);
					
					msg.setUserId(userId.getText());
					msg.setPassword(password.getText());
					outObj.writeObject(msg);
	
					Message response = (Message) inObj.readObject();
	
					if (response.getType() == MessageType.Login) {
						if (response.getUser() != null) {
							currUser = response.getUser();
							rooms = response.getRooms();
							users = response.getUserList();
						} else {
							JOptionPane.showMessageDialog(
								null,
								response.getContents(),
								"Login Error",
								JOptionPane.ERROR_MESSAGE
							);
						}
					}
				} else if (result == JOptionPane.CANCEL_OPTION) {
					System.exit(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
    	