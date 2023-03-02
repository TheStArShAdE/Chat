import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_v001 {
	static List<ClientHandler> list = new LinkedList<>();
	public static void main(String[]args)throws Exception {
		ServerSocket serverSocket = new ServerSocket(8080);
		Socket socket;
		while (true) {
			socket = serverSocket.accept();
			System.out.println("New Client Recieved: " + socket);
			DataInputStream inputStream = new DataInputStream(socket.getInputStream());
			DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
			String name = inputStream.readUTF();
			ClientHandler match = new ClientHandler(socket, name, inputStream, outputStream);
			list.add(match);
			Thread thread = new Thread(match);
			System.out.println(name + " added to active client list.");
			thread.start();
		}
	}
}
class ClientHandler implements Runnable {
	Scanner scan = new Scanner(System.in);
	private String name;
	DataInputStream inputStream;
	DataOutputStream outputStream;
	Socket socket;
	boolean isLogged;
	public ClientHandler(Socket socket, String name, DataInputStream inputStream, DataOutputStream outputStream) {
		this.socket = socket;
		this.name = name;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.isLogged = true;
	}
	@Override
	public void run() {
		String stream;
		while (true) {
			try {
				String sender = "";
				stream = inputStream.readUTF();
				if (stream.equalsIgnoreCase("Logout")) {
					for (ClientHandler handler : Server_v001.list)
						if ((handler.socket.toString()).equals(this.socket.toString())) {
							sender = handler.name;
							handler.outputStream.writeUTF(stream);
							Server_v001.list.remove(handler);
						}
					System.out.println(sender + " disconnected.");
					this.isLogged = false;
					this.socket.close();
					break;
				}
				StringTokenizer tokenizer = new StringTokenizer(stream, "#");
				String sendMessage = tokenizer.nextToken();
				String recipient = tokenizer.nextToken();
				for (ClientHandler handler : Server_v001.list)
					if (handler.name.equals(recipient) && handler.isLogged==true) {
						handler.outputStream.writeUTF(this.name + ": " + sendMessage);
						System.out.println(this.name+" -> "+recipient);
						break;
					}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		try {
			this.inputStream.close();
			this.outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}