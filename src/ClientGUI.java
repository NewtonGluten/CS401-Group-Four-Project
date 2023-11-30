import java.util.*;
import java.awt.event.*;
import javax.swing.event.*;

import javax.swing.*;

public class ClientGUI implements Runnable {
  private ArrayList<Message> inMsgs;
  private ArrayList<Message> outMsgs;
  private List<Room> rooms;
  private List<User> users;
  private String[] roomIdList;
  private String userId;

  private JList<String> roomListComponent;
  private JList<String> msgListComponent;
  private JList<String> usersInRoomComponent;
  private JFrame mainWindow;
  private JFrame newRoomWindow;


  public ClientGUI(
    ArrayList<Message> inMsgs,
    ArrayList<Message> outMsgs,
    List<Room> rooms,
    List<User> users,
    String userId
  ) {
    this.inMsgs = inMsgs;
    this.outMsgs = outMsgs;
    this.rooms = rooms;
    this.users = users;
    this.roomIdList = null;
    this.userId = userId;

    this.roomListComponent = null;

    setRoomIdList();
    createRoomListComponent();
    createMsgListComponent();
    createUsersInRoomComponent();
    createNewRoomWindow();
    createWindow();
  }

  private void createRoomListComponent() {
    roomListComponent = new JList<String>(roomIdList);
    roomListComponent.setFixedCellWidth(256);
    roomListComponent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    roomListComponent.setSelectedIndex(0);

    roomListComponent.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (roomListComponent.getSelectedIndex() >= 0) {
          Room currentRoom = getCurrentRoom(roomListComponent.getSelectedValue());
          
          if (currentRoom != null) {
            msgListComponent.setListData(currentRoom.getMessagesAsArray());
            usersInRoomComponent.setListData(currentRoom.getUsers().toArray(new String[0]));
          }
        }
      }
    });
  }

  private void createUsersInRoomComponent() {
    usersInRoomComponent = new JList<String>();
    usersInRoomComponent.setFixedCellWidth(256);

    if (rooms.size() > 0) {
      Room currentRoom = rooms.get(roomListComponent.getSelectedIndex());

      usersInRoomComponent.setListData(currentRoom.getUsers().toArray(new String[0]));
    }
  }

  private void createMsgListComponent() {
    msgListComponent = new JList<String>();
    msgListComponent.setFixedCellWidth(256);

    if (rooms.size() > 0) {
      Room currentRoom = rooms.get(roomListComponent.getSelectedIndex());

      msgListComponent.setListData(currentRoom.getMessagesAsArray());
    }
  }

  public void run() {
    try {
      while (true) {
        if (!mainWindow.isVisible()) {
          Message msg = new Message(MessageType.Logout);

          msg.setUserId(userId);
          msg.setUserStatus(UserStatus.Offline);
          outMsgs.add(msg);

          break;
        }

        Room currentRoom = getCurrentRoom(roomListComponent.getSelectedValue());

        if (currentRoom != null) {
          if (currentRoom.hasNewMessage()) {
            msgListComponent.setListData(currentRoom.getMessagesAsArray());
          }

          if (currentRoom.usersChanged()) {
            usersInRoomComponent.setListData(currentRoom.getUsers().toArray(new String[0]));
          }

          setRoomIdList();
        }

        Thread.sleep(50);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void updateUI() {
    if (rooms.size() == 0) {
      msgListComponent.setListData(new String[0]);
      usersInRoomComponent.setListData(new String[0]);
      setRoomIdList();
    }

    Room currentRoom = getCurrentRoom(roomListComponent.getSelectedValue());

    if (currentRoom != null) {
      msgListComponent.setListData(currentRoom.getMessagesAsArray());
      usersInRoomComponent.setListData(currentRoom.getUsers().toArray(new String[0]));
      setRoomIdList();
    }
  }

  private Room getCurrentRoom(String roomId) {
    for (Room room : rooms) {
      if (room.getId().equals(roomId)) {
        return room;
      }
    }

    return null;
  }

  private void setRoomIdList() {
    if (roomIdList == null || roomIdList.length != rooms.size()) {
      roomIdList = new String[rooms.size()];

      for (int i = 0; i < rooms.size(); i++) {
        roomIdList[i] = rooms.get(i).getId();
      }

      if (roomListComponent != null) {
        roomListComponent.setListData(roomIdList);
      }
    }
  }

  private void createWindow() {
    JFrame frame = new JFrame("Chat Application");
    JPanel panel = new JPanel();
    JScrollPane scrollPane = new JScrollPane(msgListComponent);
    JTextField entryField = new JTextField(50);
    JButton sendBtn = new JButton("Send");
    JButton createRoomBtn = new JButton("Create Room");
    JButton leaveRoomBtn = new JButton("Leave Room");

    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    JScrollPane usersScrollPane = new JScrollPane(usersInRoomComponent);

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

        message.setRoomId(roomListComponent.getSelectedValue());
        message.setUserId(userId);
        rooms.remove(getCurrentRoom(message.getRoomId()));
        roomListComponent.setSelectedIndex(0);
        updateUI();
        outMsgs.add(message);
      }
    });

    createRoomBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        newRoomWindow.setVisible(true);
      }
    });

    panel.add(createScrollingRoomList());
    panel.add(scrollPane);
    panel.add(usersScrollPane);
    panel.add(entryField);

    panel.add(sendBtn);
    panel.add(leaveRoomBtn);
    panel.add(createRoomBtn);

    frame.add(panel);
    frame.pack();
    frame.setVisible(true);

    mainWindow = frame;
  }

  private void sendChat(String text) {
    Message message = new Message(MessageType.NewChat);

    message.setRoomId(roomListComponent.getSelectedValue());
    message.setUserId(userId);
    message.setContents(text);
    outMsgs.add(message);
  }

  private JScrollPane createScrollingRoomList() {
    JScrollPane roomListScrollPane = new JScrollPane(roomListComponent);
    roomListScrollPane.setViewportView(roomListComponent);
    roomListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    roomListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    return roomListScrollPane;
  }

  public void createNewRoomWindow() {
    JFrame frame = new JFrame("Create New Room");
    JPanel panel = new JPanel();
    JButton createBtn = new JButton("Create");
    JCheckBox[] checkboxes = new JCheckBox[users.size()];
    JPanel userList = new JPanel();

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

        if (userIds.size() > 0) {
          Message message = new Message(MessageType.CreateRoom);

          userIds.add(userId);
          message.setUserId(userId);
          message.setUsers(userIds);
          outMsgs.add(message);
        }

        frame.setVisible(false);
      }
    });

    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(userListScrollPane);
    panel.add(createBtn);

    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    
    frame.add(panel);
    frame.pack();
    frame.setVisible(false);

    newRoomWindow = frame;
  }
}
