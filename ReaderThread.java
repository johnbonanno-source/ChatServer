/**
 * This thread is passed a socket that it reads from. Whenever it gets input
 * it writes it to the ChatScreen text area using the displayMessage() method.

 Edited by John B, Maddy B, Lian X
 */

import java.io.*;
import java.net.*;
import javax.swing.*;

public class ReaderThread implements Runnable
{
	Socket server;
	BufferedReader fromServer;
	ChatScreen screen;

	public ReaderThread(Socket server, ChatScreen screen) {
		this.server = server;
		this.screen = screen;
	}

	public void run() {
		try {
			fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
			
			while (true) {
				String message = fromServer.readLine();
				
				if (message != null)
				screen.displayMessage(message);
			
				
			}
		}
		catch (IOException ioe) { System.out.println(ioe); }

    }
    
}