#### ClientHandler.java
- Needs to accept message objects instead of text

#### ClientGUI.java
- Needs to be able to send message objects instead of text
- Will own client and run GUI

#### Client.java
- owned by ClientGUI.java


#### Server.java
- needs to own all the big pieces:
  - UserStorage
  - RoomStorage
  - Logger
  - Authenticator

#### Overall
- unit tests
