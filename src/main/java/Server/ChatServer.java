package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {

    public static void main(String[] args) {

        ArrayList<SocketWrapper> connectedList = new ArrayList<>();
        int port = 3063;
        try (ServerSocket serverS = new ServerSocket(port)) {
            while (true) {

                Socket socket = serverS.accept();
                SocketWrapper user = new SocketWrapper("Connected User", socket);
                connectedList.add(user);
                System.out.println("Users: " + connectedList.size());

                // Send updated list to EVERYONE
                for (SocketWrapper s : connectedList) {
                    s.getOutputStream().reset();
                    s.getOutputStream().writeObject(connectedList);
                    s.getOutputStream().flush();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}