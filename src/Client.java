import java.io.*;
import java.net.Socket;
import java.util.List;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;

// Client class
class Client {
	private static User currUser = null;
	private static List<Room> rooms = null;
	private static List<User> users = null;
	private static JFrame loginWindow;
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

			// wait for the user to either login or close the login window
			while (loginWindow.isVisible()) {
				Thread.sleep(100);
			}

			// user closed the login window
			if (currUser == null) {
				return;
			}

			ClientGUI gui = new ClientGUI(inObj, outObj, rooms, users, currUser);

			Thread guiThread = new Thread(gui);


			guiThread.start();

			// Busy wait while the threads are running
			while (true) {
				// GUI thread will die when the user closes the window
				if (!guiThread.isAlive()) {

					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
		} finally {
			try {
				if (outObj != null) {
					outObj.close();
				}

				if (inObj != null) {
					inObj.close();
					socket.close();
				}
				
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void doLoginFlow(ObjectInputStream inObj, ObjectOutputStream outObj) {
		JFrame frame = new JFrame("Login");
		JPanel panel = new JPanel();
		JPanel userIdPanel = new JPanel();
		JPanel passwordPanel = new JPanel();
		JTextField userId = new JTextField(20);
		JPasswordField password = new JPasswordField(20);
		JButton loginBtn = new JButton("Login");
		
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		userIdPanel.add(new JLabel("User ID    "));
		userIdPanel.add(userId, BorderLayout.SOUTH);
		passwordPanel.add(new JLabel("Password"));
		passwordPanel.add(password, BorderLayout.SOUTH);
		
		panel.add(userIdPanel, BorderLayout.NORTH);
		panel.add(passwordPanel, BorderLayout.SOUTH);
		panel.add(loginBtn);
		
		

		loginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (userId.getText().length() > 0 && password.getText().length() > 0) {
					tryLogin(inObj, outObj, userId.getText(), password.getText());
				}
			}
		});

		password.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (userId.getText().length() > 0 && password.getText().length() > 0) {
						tryLogin(inObj, outObj, userId.getText(), password.getText());
					}
				}
			}
		});

		
		frame.setPreferredSize(new Dimension(350,150));
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	    frame.setResizable(false);

		loginWindow = frame;
	}

	private static void tryLogin(ObjectInputStream inObj, ObjectOutputStream outObj, String user, String password) {
		try {
			Message msg = new Message(MessageType.Login);
	
			msg.setUserId(user);
			msg.setPassword(password);
			outObj.writeObject(msg);

			Message response = (Message) inObj.readObject();

			if (response.getType() == MessageType.Login) {
				if (response.getUser() != null) {
					currUser = response.getUser();
					rooms = response.getRooms();
					users = response.getUserList();
					loginWindow.setVisible(false);
				} else {
					JOptionPane.showMessageDialog(null, response.getContents());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
    	
