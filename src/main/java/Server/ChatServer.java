package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {

    private static ArrayList<SocketWrapper> connectedList =
            new ArrayList<>();

    private static int userNumber = 1;

    public static void main(String[] args) {

        int port = 3073;

        try (ServerSocket serverS = new ServerSocket(port)) {

            System.out.println("Server started on port " + port);

            while (true) {

                Socket socket = serverS.accept();

                // Give every new window a different name
                String username = "User " + userNumber;
                userNumber++;

                SocketWrapper user =
                        new SocketWrapper(username, socket);

                connectedList.add(user);

                System.out.println(
                        username + " connected."
                );

                System.out.println(
                        "Users: " + connectedList.size()
                );

                // Send updated user list
                sendUserList();

                // Listen for messages from this user
                Thread thread = new Thread(() -> {
                    receiveMessages(user);
                });

                thread.setDaemon(true);
                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void receiveMessages(SocketWrapper sender) {

        try {

            ObjectInputStream input =
                    new ObjectInputStream(
                            sender.getSocket().getInputStream()
                    );

            while (true) {

                Object received = input.readObject();

                if (received instanceof ChatMessage) {

                    ChatMessage message =
                            (ChatMessage) received;

                    System.out.println(
                            message.getSender()
                            + " -> "
                            + message.getReceiver()
                            + ": "
                            + message.getMessage()
                    );

                    sendMessageToUser(message);
                }
            }

        } catch (IOException | ClassNotFoundException e) {

            System.out.println(
                    sender.getName() + " disconnected."
            );

            connectedList.remove(sender);

            try {
                sendUserList();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void sendMessageToUser(
            ChatMessage message) throws IOException {

        for (SocketWrapper user : connectedList) {

            // Find ONLY the intended receiver
            if (user.getName().equals(message.getReceiver())) {

                ObjectOutputStream output =
                        user.getOutputStream();

                output.writeObject(message);
                output.flush();

                System.out.println(
                        "Message delivered to "
                        + user.getName()
                );

                break;
            }
        }
    }

    private static void sendUserList() throws IOException {

        for (SocketWrapper user : connectedList) {

            ObjectOutputStream output =
                    user.getOutputStream();

            output.reset();

            output.writeObject(connectedList);

            output.flush();
        }
    }
}