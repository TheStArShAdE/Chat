import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client_v001 {
	static final int serverPort = 0;

      public static void main(String[] args) throws Exception {
            Scanner scan = new Scanner(System.in);
		InetAddress inet = InetAddress.getByName("10.2.2.2");
        	Socket socket = new Socket(inet, 8080);
        	DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        	DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
		System.out.println("Enter your username (whitespaces not allowed)");
		outputStream.writeUTF(scan.nextLine());
        	Thread send = new Thread(new Runnable() {
            	@Override
			public void run() {
                		while(true) {
                    		String message = scan.nextLine();
                    		try {
                        		outputStream.writeUTF(message);
						if (message.equalsIgnoreCase("logout")) {
							return;
						}
                    		} catch (Exception e) {
                        		System.out.println(e);
                    		}
                		}
            	}
        	});
        	Thread read = new Thread(new Runnable() {
            	@Override
			public void run() {
            	try {
				while (true){
                        	String message = inputStream.readUTF();
					if (message.equalsIgnoreCase("logout"))
						return;
					else
                        		System.out.println(message);
				}
			} catch (Exception e) {
                  	System.out.println(e);
                  }
		}
        });
        send.start();
        read.start();
    }
}
