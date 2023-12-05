import java.util.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;

import javax.swing.*;

public class ClientGUI implements Runnable {
  private ObjectInputStream objIn;
  private ObjectOutputStream objOut;
  private List<Room> rooms;
  private List<User> users;
  private User currUser;
  private String userId;

  JTextField searchField;
  JButton searchBtn;
  private boolean isSearching;

  private JList<String> roomsDisplay;
  private JFrame mainWindow;
  private JFrame newRoomWindow;
  private JFrame addUserWindow;
  private JTextArea logDisplay;
  private JFrame logWindow;

  private JLabel messageHistory;
  private JTextArea msgDisplay;
  private JTextArea usersDisplay;

  public ClientGUI(
    ObjectInputStream objIn,
    ObjectOutputStream objOut,
    List<Room> rooms,
    List<User> users,
    User currUser
  ) {
    this.objIn = objIn;
    this.objOut = objOut;
    this.rooms = rooms;
    this.users = users;
    this.currUser = currUser;
    this.userId = currUser.getId();
    this.isSearching = false;

    searchField = new JTextField(30);
    searchBtn = new JButton("Search");
    messageHistory = new JLabel("Messages");

    createRoomsDisplay();
    createMsgDisplay();
    createUsersDisplay();
    createNewRoomWindow();
    createAddUserWindow();

    this.logDisplay = null;

    if (currUser.getRole() == UserRole.IT) {
      createLogWindow();
    }

    createMainWindow();
  }

  public void run() {
    try {
      ClientReader reader = new ClientReader(
        objIn,
        rooms,
        users,
        userId,
        roomsDisplay,
        msgDisplay,
        usersDisplay,
        logDisplay
      );
      Thread readerThread = new Thread(reader);

      readerThread.start();
      roomsDisplay.setSelectedIndex(0);

      while (true) {
        if (!mainWindow.isVisible()) {
          Message msg = new Message(MessageType.Logout);

          msg.setUserId(userId);
          msg.setUserStatus(UserStatus.Offline);
          sendMsg(msg);

          readerThread.interrupt();

          break;
        }

        Thread.sleep(50);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  //
  // Window creation methods
  //

  // Creates the main window (frame) that holds:
  // - The list of rooms
  // - The list of users in the current room
  // - The text area for displaying messages
  // - The text field for entering messages
  // - The send button
  // - The leave room button
  // - The add user button
  // - The create room button
  // - The status box
  // - The view logs button (only visible to IT users)
  private void createMainWindow() {
    JFrame frame = new JFrame("Chat Application" + " - " + userId);
    
    
    JPanel roomPanel = new JPanel();
    JPanel roomScrollPanel = new JPanel(new GridLayout());
    JPanel roomOptionsPanel = new JPanel(new FlowLayout());
    JPanel centerPanel = new JPanel();
    JPanel inputTextPanel = new JPanel();
    JPanel searchPanel = new JPanel();
    JPanel searchFieldPanel = new JPanel();
    JPanel headerPanel = new JPanel();
    JPanel textFieldPanel = new JPanel();
    JPanel userPanel = new JPanel();
    
    
    JScrollPane scrollPane = new JScrollPane(msgDisplay);
    JScrollPane usersScrollPane = new JScrollPane(usersDisplay);

    
    JTextField entryField = new JTextField(27);
    searchField.setColumns(25);
    
    

    
    JButton sendBtn = new JButton("Send");
    JButton createRoomBtn = new JButton("Create Room");
    JButton addUserBtn = new JButton("Add User");
    JButton leaveRoomBtn = new JButton("Leave Room");
    JButton logBtn = new JButton("View Logs");
    logBtn.setVisible(false);
    
    
    JLabel currentRoomLabel = new JLabel("CURRENT ROOM: INSERT ROOM HERE");
    JLabel setStatusLabel = new JLabel("Set status");
    JLabel usersInRoomLabel = new JLabel("Users in room");
    JLabel roomList = new JLabel("Room List");


    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    frame.setLocationRelativeTo(null);

    searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
    inputTextPanel.setLayout(new BoxLayout(inputTextPanel, BoxLayout.X_AXIS));

    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


    
    searchBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Room currentRoom = getCurrentRoom();

        if (currentRoom != null) {
          if (isSearching) {
            isSearching = false;
            searchBtn.setText("Search");
            searchField.setText("");
            searchField.setEditable(true);
            updateMsgDisplay(currentRoom);
          } else {
            String text = searchField.getText();
  
            if (text.length() > 0) {
              doSearch(currentRoom, text);
              isSearching = true;
              searchBtn.setText("Cancel");
              searchField.setEditable(false);

            }
          }
        }

      }
    });

    sendBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String text = entryField.getText();

        if (text.length() > 0) {
          sendChat(text);
          entryField.setText("");
        }
      }
    });

    entryField.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          String text = entryField.getText();

          if (text.length() > 0) {
            sendChat(text);
            entryField.setText("");
          }
        }
      }
    });

    leaveRoomBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Message message = new Message(MessageType.LeaveRoom);


        message.setRoomId(getCurrentRoomId());
        message.setUserId(userId);
        rooms.remove(getCurrentRoom());
        sendMsg(message);

        if (rooms.size() > 0) {
          updateRoomsDisplay();
          roomsDisplay.setSelectedIndex(0);
          updateMsgDisplay(getCurrentRoom());
          updateUserDisplay(getCurrentRoom());
        } else {
          updateRoomsDisplay();
          msgDisplay.setText("");
          usersDisplay.setText("");
        }
      }
    });

    createRoomBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        newRoomWindow.setVisible(true);
      }
    });
    
    addUserBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addUserWindow.setVisible(true);
      }
    });
    
    // Room panel which is set to WEST on the frame
    // Has the following: 	room scroll
    //						create room button
    //						add user to room button
    //						Leave room button
    roomScrollPanel.add(createRoomsScrollPane());
    roomOptionsPanel.add(createRoomBtn);
    roomOptionsPanel.add(addUserBtn);
    roomOptionsPanel.add(leaveRoomBtn);
    roomOptionsPanel.add(logBtn);
    
    roomPanel.add(roomList);
    roomPanel.add(roomScrollPanel, BorderLayout.NORTH);
    roomPanel.add(roomOptionsPanel, BorderLayout.SOUTH);


    headerPanel.add(currentRoomLabel);
    
    JComboBox<String> statusBox = createStatusBox();
    userPanel.add(usersInRoomLabel);
    userPanel.add(usersScrollPane, BorderLayout.NORTH);
    userPanel.add(setStatusLabel);
    userPanel.add(statusBox, BorderLayout.SOUTH);
    
    searchFieldPanel.add(searchField);
    searchPanel.add(searchFieldPanel);
    searchPanel.add(searchBtn);
    textFieldPanel.add(entryField);
    inputTextPanel.add(textFieldPanel);
    inputTextPanel.add(sendBtn);
    
    centerPanel.add(messageHistory);
    centerPanel.add(searchPanel);
    centerPanel.add(scrollPane, BorderLayout.NORTH);
    centerPanel.add(inputTextPanel, BorderLayout.SOUTH);

    
    if (currUser.getRole() == UserRole.IT) {

      logBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          logWindow.setVisible(true);
        }
      });

      logBtn.setVisible(true);
    }
    // Button sizes
    createRoomBtn.setPreferredSize(new Dimension(170,30));
    addUserBtn.setPreferredSize(new Dimension(170,30));
    leaveRoomBtn.setPreferredSize(new Dimension(170,30));
    logBtn.setPreferredSize(new Dimension(170,30));
    
    
    // FRAME AND PANEL SIZE SETTINGS
    // VERY PARTICULAR
    //						800
    // ------------------------------------------------------------
    // |                                                          |
    // |----------------------------------------------------------|
    // |          |                                |              |
    // |          |                                |              | 
    // |          |                                |              |
    // |          |                                |              |  600
    // |          |                                |              |
    // |          |                                |              |
    // |----------|                                |              |
    // |          |                                |              |
    // |          |                                |              |
    // ------------------------------------------------------------
    //
    frame.setPreferredSize(new Dimension			(800, 600));

    roomScrollPanel.setPreferredSize(new Dimension	(180, 380));
    roomOptionsPanel.setPreferredSize(new Dimension	(180, 180));
    
    centerPanel.setPreferredSize(new Dimension		(380, 600));
    scrollPane.setPreferredSize(new Dimension		(360, 440));
    
    userPanel.setPreferredSize(new Dimension 		(200, 600));
    usersScrollPane.setPreferredSize(new Dimension 	(180, 400));
    statusBox.setPreferredSize(new Dimension		(180, 40));
    
    
    
    
    
    headerPanel.setPreferredSize(new Dimension		(780, 30 ));
    
    roomPanel.setPreferredSize(new Dimension		(200, 500));

    
    frame.getContentPane().add(roomPanel, BorderLayout.WEST);
    frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
    frame.getContentPane().add(userPanel, BorderLayout.EAST);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    frame.setResizable(false);


    mainWindow = frame;
  }

  // Creates the window (frame) that allows the user to create a new room
  // Users are presented with a list of checkboxes, one for each user
  // Not visible by default
  public void createNewRoomWindow() {
    JFrame frame = new JFrame("Create New Room");
    JPanel panel = new JPanel();
    JPanel roomNamePanel = new JPanel();
    JPanel searchUserPanel = new JPanel();
    JButton createBtn = new JButton("Create");
    JButton searchUserBtn = new JButton("Search");
    JLabel roomNameLabel = new JLabel  ("Room Name  ");
    JLabel searchNameLabel = new JLabel("Search User");

    JCheckBox[] checkboxes = new JCheckBox[users.size()];
    JPanel userList = new JPanel();
    JTextField roomNameField = new JTextField(22);
    JTextField searchUserField = new JTextField(22);

    roomNameField.setText(userId + "'s Room");

    for (int i = 0; i < users.size(); i++) {
      checkboxes[i] = new JCheckBox(users.get(i).getId());
      userList.add(checkboxes[i]);
    }

    JScrollPane userListScrollPane = new JScrollPane(userList);

    userListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    userListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    userList.setLayout(new BoxLayout(userList, BoxLayout.Y_AXIS));
    
    searchUserBtn.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
            
            // 1) take the user input which is the users we want to search for
            // 2) get all of the userIds with the search parameters
            // 3) repaint the user list with the updated users
            // 4) make the search box into "Cancel"
            // 5) make the text field not editable
            String input = searchUserField.getText();
            
            // Validate input size
            if(!(input.length() > 0)) {
            	return;
            }
            // if we have already searched;
            if(searchUserBtn.getText().equals("Cancel")) {
            	searchUserBtn.setText("Search");
            	searchUserField.setEditable(true);

            	for (int i = 0; i < users.size(); i++) {
        			checkboxes[i].setVisible(true);

            	}
           	
            }
            // else search
            else {
            	searchUserBtn.setText("Cancel");
            	searchUserField.setEditable(false);

            	for(int i = 0; i < users.size(); i++) {
            		if(users.get(i).getId().contains(input)) {
            			checkboxes[i].setVisible(true);
            		}
            		else {
            			checkboxes[i].setVisible(false);
            		}
            	}          	
            }
            
    	}
    });
    createBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ArrayList<String> userIds = new ArrayList<String>();

        for (int i = 0; i < users.size(); i++) {
          if (checkboxes[i].isSelected()) {
            userIds.add(users.get(i).getId());
            checkboxes[i].setSelected(false);
          }
        }

        if (userIds.size() > 0 && roomNameField.getText().length() > 0) {
          Message message = new Message(MessageType.CreateRoom);

          userIds.add(userId);
          message.setUserId(userId);
          message.setContents(roomNameField.getText());
          message.setUsers(userIds);
          sendMsg(message);
          roomNameField.setText(userId + "'s Room");
        }

        frame.setVisible(false);
      }
    });

    
    roomNamePanel.add(roomNameLabel);
    roomNamePanel.add(roomNameField);
    
    searchUserPanel.add(searchNameLabel);
    searchUserPanel.add(searchUserField);
    searchUserPanel.add(searchUserBtn);

    //panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(roomNamePanel);
    panel.add(searchUserPanel, BorderLayout.NORTH);
    panel.add(userListScrollPane, BorderLayout.CENTER);
    panel.add(createBtn, BorderLayout.SOUTH);
    

    frame.setPreferredSize(new Dimension(400, 460));
    userListScrollPane.setPreferredSize(new Dimension(380, 300));
    
    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    
    frame.add(panel);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setResizable(false);
    frame.setVisible(false);

    newRoomWindow = frame;
  }

  // Creates the window (frame) that allows IT users to view logs, containing:
  // - A text field and button for getting logs for a specific user
  // - A text field and button for getting logs for a specific room
  // - A text area for displaying the logs
  // Not visible by default
  private void createLogWindow() {
    JFrame frame = new JFrame("Application Logs");
    JPanel panel = new JPanel();
    JPanel userIdPanel = new JPanel();
    JPanel roomIdPanel = new JPanel();
    JTextField usernameField = new JTextField(25);
    JTextField roomIdField = new JTextField(25);
    JButton userLogsBtn = new JButton("Get User Logs");
    JButton roomLogsBtn = new JButton("Get Room Logs");

    logDisplay = new JTextArea(20, 75);
    logDisplay.setEditable(false);
    logDisplay.setLineWrap(true);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    JScrollPane logScrollPane = new JScrollPane(logDisplay);

    logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    logScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    userLogsBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();

        if (username.length() > 0) {
          Message message = new Message(MessageType.GetLogs);

          message.setUserId(userId);
          message.setContents(username);
          sendMsg(message);
          usernameField.setText("");
          roomIdField.setText("");
        }
      }
    });

    roomLogsBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String roomId = roomIdField.getText();

        if (roomId.length() > 0) {
          Message message = new Message(MessageType.GetLogs);

          message.setUserId(userId);
          message.setRoomId(roomId);
          sendMsg(message);
          usernameField.setText("");
          roomIdField.setText("");
        }
      }
    });

    userLogsBtn.setPreferredSize(new Dimension(130, 25));
    roomLogsBtn.setPreferredSize(new Dimension(130, 25));
    
    userIdPanel.add(new JLabel("Username"));
    userIdPanel.add(usernameField, BorderLayout.WEST);
    userIdPanel.add(userLogsBtn, BorderLayout.EAST);
    
    roomIdPanel.add(new JLabel("Room ID    "));
    roomIdPanel.add(roomIdField, BorderLayout.WEST);
    roomIdPanel.add(roomLogsBtn, BorderLayout.EAST);
    
    panel.add(userIdPanel);
    panel.add(roomIdPanel);
    panel.add(logScrollPane);
/*
 *  panel.add(new JLabel("Username"));
    panel.add(usernameField);
    panel.add(userLogsBtn);
    panel.add(new JLabel("Room ID"));
    panel.add(roomIdField);
    panel.add(roomLogsBtn);
 */


    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    frame.add(panel);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(false);

    logWindow = frame;
  }

  private void createAddUserWindow() {
    JFrame frame = new JFrame("Add User");
    JPanel topPanel = new JPanel();
    JPanel bottomPanel = new JPanel();
    JButton addUserBtn = new JButton("Add User");
    
    JTextField usernameField = new JTextField(22);
    
    JTextArea userIdsArea = new JTextArea(15, 20);
    
    for (int i = 0; i < users.size(); i++) {
    	userIdsArea.append(users.get(i).getId() + '\n');
      }

    addUserBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();

        if (username.length() > 0) {
          if (getCurrentRoom().getUsers().contains(username)) {
            JOptionPane.showMessageDialog(
              frame,
              "User is already in the room",
              "Error",
              JOptionPane.ERROR_MESSAGE
            );
            return;
          } else if (userExists(username)) {
            Message message = new Message(MessageType.AddToRoom);
  
            message.setUserId(userId);
            message.setContents(username);
            message.setRoomId(getCurrentRoomId());
            sendMsg(message);
            usernameField.setText("");
  
            addUserWindow.setVisible(false);
          } else {
            JOptionPane.showMessageDialog(
              frame,
              "User does not exist",
              "Error",
              JOptionPane.ERROR_MESSAGE
            );
          }
        }
      }
    });
    usernameField.getDocument().addDocumentListener(new DocumentListener() {
    	public void changedUpdate(DocumentEvent e) {
    		updateText();
    	}
    	public void removeUpdate(DocumentEvent e) {
    		updateText();
    	}
    	public void insertUpdate(DocumentEvent e) {
    		updateText();
    	}
    	public void updateText() {
    		userIdsArea.setText("");
    		String input = usernameField.getText();
    		for (int i = 0; i < users.size(); i++) {
    			String name = users.get(i).getId();
    			if(name.contains(input)) {
    				userIdsArea.append(users.get(i).getId() + '\n');
        		}
        	}
    	}
    });
    

    topPanel.add(new JLabel("Username"));
    topPanel.add(usernameField, BorderLayout.NORTH);
    topPanel.add(addUserBtn, BorderLayout.SOUTH);
    
    bottomPanel.add(userIdsArea);
    
    frame.setPreferredSize(new Dimension(400,330));
    topPanel.setPreferredSize(new Dimension(400,40));
    bottomPanel.setPreferredSize(new Dimension(380,250));
    userIdsArea.setPreferredSize(new Dimension(370,230));

    
    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    frame.add(topPanel, BorderLayout.NORTH);
    frame.add(bottomPanel);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setResizable(false);
    frame.setVisible(false);

    addUserWindow = frame;
  }
  //
  // Individual component creation methods
  //

  // Creates the list of rooms
  private void createRoomsDisplay() {
    roomsDisplay = new JList<String>();
    roomsDisplay.setFixedCellWidth(256);
    roomsDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    roomsDisplay.setSelectedIndex(0);

    roomsDisplay.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (roomsDisplay.getSelectedIndex() >= 0) {
          Room currentRoom = getCurrentRoom();
          
          if (currentRoom != null) {
            if (isSearching) {
              isSearching = false;
              searchBtn.setText("Search");
              searchField.setText("");
            }

            updateMsgDisplay(currentRoom);
            updateUserDisplay(currentRoom);
          }
        }
      }
    });

    updateRoomsDisplay();
  }

  // Creates the scroll pane that holds the list of rooms
  private JScrollPane createRoomsScrollPane() {
    JScrollPane roomsScrollPane = new JScrollPane(roomsDisplay);
    roomsScrollPane.setViewportView(roomsDisplay);
    roomsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    roomsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    return roomsScrollPane;
  }

  
  // Creates the text area for displaying messages of the current room
  private void createMsgDisplay() {
    msgDisplay = new JTextArea(5, 32);
    msgDisplay.setEditable(false);
    msgDisplay.setLineWrap(true);

    Room currentRoom = getCurrentRoom();

    if (currentRoom != null) {
      updateMsgDisplay(getCurrentRoom());
    }
  }

  // Creates the text area for displaying users in the current room
  private void createUsersDisplay() {
    usersDisplay = new JTextArea();
    usersDisplay.setEditable(false);
    usersDisplay.setLineWrap(true);

    Room currentRoom = getCurrentRoom();

    if (currentRoom != null) {
      updateUserDisplay(getCurrentRoom());
    }
  }

  // Creates the status box that users can change their status from
  private JComboBox<String> createStatusBox() {
    String[] statuses = {"Online", "Away", "Busy"};
    JComboBox<String> statusBox = new JComboBox<String>(statuses);

    statusBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Message message = new Message(MessageType.ChangeStatus);
        UserStatus status = UserStatus.Online;

        switch (statusBox.getSelectedItem().toString()) {
          case "Away":
            status = UserStatus.Away;
            break;
          case "Busy":
            status = UserStatus.Busy;
            break;
          default:
            break;
        }

        message.setUserId(userId);
        message.setUserStatus(status);
        sendMsg(message);
      }
    });

    return statusBox;
  }
  
  //
  // Component update methods
  //

  private void updateUserDisplay(Room room) {
    usersDisplay.setText("");

    List<String> users = room.getUsers();

    for (String user : users) {
      usersDisplay.append(getUserWithStatus(user) + "\n");
    }
  }

  private void updateMsgDisplay(Room room) {
    msgDisplay.setText("");
    messageHistory.setText(room.getTitle());

    String[] messages = room.getMessagesAsArray();

    for (String message : messages) {
      msgDisplay.append(message + "\n");
    }
  }

  private void updateRoomsDisplay() {
    String[] roomIds = new String[rooms.size()];

    for (int i = 0; i < rooms.size(); i++) {
      roomIds[i] = rooms.get(i).getTitle();
    }

    roomsDisplay.setListData(roomIds);
  }

  // Helper methods

  // Called when the user clicks the "Send" button or presses enter
  private void sendChat(String text) {
    Message message = new Message(MessageType.NewChat);

    message.setRoomId(getCurrentRoomId());
    message.setUserId(userId);
    message.setContents(text);
    sendMsg(message);
  }

  // Sends a message to the server
  // Has it's own helper method to avoid placing try/catch blocks everywhere
  private void sendMsg(Message msg) {
    try {
      objOut.writeObject(msg);
    } catch (SocketException e) {
      System.exit(0);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Returns a nice string of the user's ID and status
  // For use in displaying in the user list
  String getUserWithStatus(String userId) {
    for (User user : users) {
      if (user.getId().equals(userId)) {
        String status = "Online";

        switch (user.getStatus()) {
          case Away:
            status = "Away";
            break;
          case Busy:
            status = "Busy";
            break;
          case Offline:
            status = "Offline";
            break;
          default:
            break;
        } 

        return user.getId() + " [" + status + "]";
      }
    }

    return userId;
  }

  private Room getCurrentRoom() {
    if (rooms.size() == 0) {
      return null;
    }

    int i = roomsDisplay.getSelectedIndex() < 0 ? 0 : roomsDisplay.getSelectedIndex();
    String selectedRoomId = rooms.get(i).getId();

    for (Room room : rooms) {
      if (room.getId().equals(selectedRoomId)) {
        return room;
      }
    }

    return null;
  }

  private String getCurrentRoomId() {
    Room room = getCurrentRoom();

    if (room != null) {
      return room.getId();
    }

    return null;
  }

  private boolean userExists(String userId) {
    for (User user : users) {
      if (user.getId().equals(userId)) {
        return true;
      }
    }

    return false;
  }

  private void doSearch(Room room, String text) {
    List<String> contents = new ArrayList<String>();
    String[] messages = room.getMessagesAsArray();

    if (text.contains("from_user:") && text.substring(0, 10).equals("from_user:")) {
      // 11 is the length of "from_user:"
      String user = text.substring(11);

      for (String msg : messages) {
        if (msg.contains(user)) {
          contents.add(msg);
        }
      }
    } else {
      for (String msg : messages) {
        if (msg.contains(text)) {
          contents.add(msg);
        }
      }
    }

    msgDisplay.setText("");

    if (contents.size() > 0) {
      for (String msg : contents) {
        msgDisplay.append(msg + "\n");
      }
    } else {
      msgDisplay.append("No results found");
    }
  }
}
