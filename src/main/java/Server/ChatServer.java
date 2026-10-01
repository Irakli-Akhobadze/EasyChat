package Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	
	static List<Socket> socketList = new ArrayList<Socket>();
	
	public static void sendMessage(Socket sender, String message) {
		for (Socket s: socketList) {
			if (s != sender) {
				
				try {
					PrintWriter pW = new PrintWriter(new OutputStreamWriter(sender.getOutputStream()), true);
					pW.println(message);
				}
				
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		int port = 5010;
		
		try (ServerSocket serverS = new ServerSocket(port)) {
			while(true) {
				Socket socket = serverS.accept();
				
				socketList.add(socket);
				
				System.out.println("New Socket Has Connected!");
				
				Thread thread = new Thread (() -> {
					try {
						BufferedReader bF = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						
						while(true) {
							String message = bF.readLine();
							
							if (message == null) {
								break;
							}
							
							System.out.println("Message: " + message);
                            sendMessage(socket, message);
						}
						
					} catch(IOException e) {
						e.printStackTrace();
					}
				});
				
				thread.start();
			}
		}
	}
}