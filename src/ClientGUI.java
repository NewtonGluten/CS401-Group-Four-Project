import java.util.*;
import java.awt.event.*;
import javax.swing.*;

public class ClientGUI implements Runnable {
  private ArrayList<Message> inMsgs;
  private ArrayList<Message> outMsgs;
  private List<Room> rooms;
  private String userId;
  private HashMap<String, Integer> chatMsgIndex;

  public ClientGUI(
    ArrayList<Message> inMsgs,
    ArrayList<Message> outMsgs,
    List<Room> rooms,
    String userId
  ) {
    this.inMsgs = inMsgs;
    this.outMsgs = outMsgs;
    this.rooms = rooms;
    this.userId = userId;
    this.chatMsgIndex = new HashMap<String, Integer>();

    for (Room room : rooms) {
      chatMsgIndex.put(room.getId(), 0);
    }
  }
  
  public void run() {
    try {
      String currentRoomId = "d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042";
      Room currentRoom = getCurrentRoom(currentRoomId);
  
      JTextArea msgList = new JTextArea(20, 50);
      msgList.setEditable(false);
      createChatWindow(msgList);
  
      if (currentRoom != null) {
        while (true) {
          int msgIndex = chatMsgIndex.get(currentRoomId);
          List<ChatMessage> chats = currentRoom.getMessages();
    
          if (msgIndex < chats.size()) {
            for (int i = msgIndex; i < chats.size(); i++) {
              ChatMessage chat = chats.get(i);
              String sender = chat.getSender();
              String contents = chat.contents();
    
              msgList.append(sender + ": " + contents + "\n");
            }
    
            chatMsgIndex.put(currentRoomId, chats.size());
          }
  
          Thread.sleep(50);
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
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

  private void createChatWindow(JTextArea msgList) {
    JFrame frame = new JFrame("Chat Application");
    JPanel panel = new JPanel();
    JScrollPane scrollPane = new JScrollPane(msgList);
    JTextField entryField = new JTextField(50);
    JButton sendBtn = new JButton("Send");

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
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

    panel.add(scrollPane);
    panel.add(entryField);
    panel.add(sendBtn);

    frame.add(panel);
    frame.pack();
    frame.setVisible(true);
  }

  private void sendChat(String text) {
    Message message = new Message(MessageType.NewChat);

    message.setRoomId("d63dbe8e-d1f3-4e82-b4de-bf2ce3c32042");
    message.setUserId(userId);
    message.setContents(text);
    outMsgs.add(message);
  }
}
