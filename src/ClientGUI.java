import java.util.*;
import java.awt.BorderLayout;
import java.awt.Color;
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

  private JList<String> roomsDisplay;
  private JFrame mainWindow;
  private JFrame newRoomWindow;
  private JFrame addUserWindow;
  private JTextArea logDisplay;
  private JFrame logWindow;

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
    JPanel headerPanel = new JPanel();
    JPanel textFieldPanel = new JPanel();
    JPanel userPanel = new JPanel();
    
    
    JScrollPane scrollPane = new JScrollPane(msgDisplay);
    JScrollPane usersScrollPane = new JScrollPane(usersDisplay);

    
    JTextField entryField = new JTextField(30);
    
    

    
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
    JLabel messageHistory = new JLabel("Messages");


    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    frame.setLocationRelativeTo(null);

    inputTextPanel.setLayout(new BoxLayout(inputTextPanel, BoxLayout.X_AXIS));

    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


    
    

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
    
    textFieldPanel.add(entryField);
    inputTextPanel.add(textFieldPanel);
    inputTextPanel.add(sendBtn);
    
    centerPanel.add(messageHistory);
    centerPanel.add(scrollPane, BorderLayout.NORTH);
    centerPanel.add(inputTextPanel, BorderLayout.SOUTH);

    
    if (currUser.getRole() == UserRole.IT) {

      logBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          logWindow.setVisible(true);
        }
      });

      logBtn.setVisible(true);
      //mainPanel.add(logBtn);
    }
    // Button sizes
    createRoomBtn.setPreferredSize(new Dimension(170,30));
    addUserBtn.setPreferredSize(new Dimension(170,30));
    leaveRoomBtn.setPreferredSize(new Dimension(170,30));
    logBtn.setPreferredSize(new Dimension(170,30));
    
    //sendBtn.setPreferredSize(new Dimension(80, 20));
    
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
    
    centerPanel.setPreferredSize(new Dimension		(400, 600));
    scrollPane.setPreferredSize(new Dimension		(380, 470));
    
    userPanel.setPreferredSize(new Dimension 		(200, 600));
    usersScrollPane.setPreferredSize(new Dimension 	(180, 400));
    statusBox.setPreferredSize(new Dimension		(180, 40));
    
    
    
    
    
    headerPanel.setPreferredSize(new Dimension		(780, 30 ));
    
    roomPanel.setPreferredSize(new Dimension		(200, 500));
    
    //roomPanel.setBackground(Color.gray);
    //roomScrollPanel.setBackground(Color.gray);

    frame.getContentPane().setBackground(Color.blue);
    
    //frame.getContentPane().add(headerPanel, BorderLayout.NORTH);
    frame.getContentPane().add(roomPanel, BorderLayout.WEST);
    frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
    frame.getContentPane().add(userPanel, BorderLayout.EAST);
    //frame.add(mainPanel);
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
    JButton createBtn = new JButton("Create");
    JCheckBox[] checkboxes = new JCheckBox[users.size()];
    JPanel userList = new JPanel();
    JTextField roomNameField = new JTextField(32);

    roomNameField.setText(userId + "'s Room");

    for (int i = 0; i < users.size(); i++) {
      checkboxes[i] = new JCheckBox(users.get(i).getId());
      userList.add(checkboxes[i]);
    }

    JScrollPane userListScrollPane = new JScrollPane(userList);

    userListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    userListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    userList.setLayout(new BoxLayout(userList, BoxLayout.Y_AXIS));

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

    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(new JLabel("Room Name"));
    panel.add(roomNameField);
    panel.add(userListScrollPane);
    panel.add(createBtn);
    
    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    
    frame.add(panel);
    frame.pack();
    frame.setLocationRelativeTo(null);
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
    JTextField usernameField = new JTextField(32);
    JTextField roomIdField = new JTextField(32);
    JButton userLogsBtn = new JButton("Get User Logs");
    JButton roomLogsBtn = new JButton("Get Room Logs");

    logDisplay = new JTextArea(20, 50);
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

    panel.add(new JLabel("Username"));
    panel.add(usernameField);
    panel.add(userLogsBtn);
    panel.add(new JLabel("Room ID"));
    panel.add(roomIdField);
    panel.add(roomLogsBtn);
    panel.add(logScrollPane);

    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    frame.add(panel);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(false);

    logWindow = frame;
  }

  private void createAddUserWindow() {
    JFrame frame = new JFrame("Add User");
    JPanel panel = new JPanel();
    JButton addUserBtn = new JButton("Add User");
    JTextField usernameField = new JTextField(32);

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

    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(new JLabel("Username"));
    panel.add(usernameField);
    panel.add(addUserBtn);

    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    frame.add(panel);
    frame.pack();
    frame.setLocationRelativeTo(null);
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
}
