/**
 * An chat server listening on port 10,000
 *
 * @author - John B, Maddy B, Lian X
 */

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;


public class  chatRoom
{
    public static final int DEFAULT_PORT = 10000;

    // construct a thread pool for concurrency
    private static final Executor exec = Executors.newCachedThreadPool();
    
    
    public static void main(String[] args) throws IOException {
        ServerSocket sock = null;
        HashMap<String,BufferedOutputStream> map = new HashMap<String,BufferedOutputStream>(25); //stores usernames and outputstreams of clients
        
        try {
            // establish the socket
            sock = new ServerSocket(DEFAULT_PORT);

            while (true) {
                /**
                 * now listen for connections
                 * and service the connection in a separate thread.
                 */
                Runnable task = new Connection(sock.accept(),map);
                exec.execute(task);
            }
        }
        catch (IOException ioe) { System.err.println(ioe); }
        finally {
            if (sock != null)
                sock.close();
        }
    }
}

class Handler
{

    /**
     * this method is invoked by a separate thread
     */
    public void process(Socket client, HashMap<String,BufferedOutputStream> map) throws java.io.IOException {
        BufferedOutputStream toClient = null;
        BufferedReader fromClient = null;
        String userName = null;
        String message;
        byte[] buffer = new byte[10000];
        
        try { 
            while(true){
            fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            toClient = new BufferedOutputStream(client.getOutputStream());
            message = fromClient.readLine() + "\r\n";
                
                /*debugging print statements to see on server side
                    System.out.println(message.split(" ")[0]);
                    System.out.println(map);
                    System.out.println(map.get(userName));
                */

                //add new user to map and write the intro user statement to the clients
                if (message.split(" ")[0].equals("JOIN")){
                    //System.out.println(true);
                    userName = message.split(" ")[1];
                    //System.out.println(userName);
                    map.put(userName, toClient);
                    //System.out.println(map);
                     
                    BufferedOutputStream users = null;
                    for (String user : map.keySet()){
                        
                        users = map.get(user);
                        users.write((userName + "has Joined the chat. \r\n").getBytes());
                        users.flush();
                    } 
                }
                //broadcast logoff message when user clicks exit from ChatScreen
                else if (message.split(" ")[0].equals("LOGOFF")){
                    BufferedOutputStream users = null;
                    for (String user : map.keySet()){
                        users = map.get(user);
                        users.write((userName + "has left the chat. \r\n").getBytes());
                        users.flush();
                    }
                }
                //broadcast a message from client
                else{
                    System.out.println(" Broadcast: "+ message);
                    //truncated msg    
                    String msg = message.split(" ",2)[1];
                    System.out.println(msg);
                    BufferedOutputStream users = null;
                    for (String user : map.keySet()){
                        users = map.get(user);
                        System.out.println(msg);
                        if(msg != null){
                        users.write(msg.getBytes());
                        users.flush();
                        }
                    }
                }
            }
                
        }
        catch (IOException ioe) {
            System.err.println(ioe);
        }
        finally {
            // close streams and socket
            if (toClient != null)
                toClient.close();
        }
    }
    
}

class Connection implements Runnable
{
    private Socket client;
    private static Handler handler = new Handler();
    private HashMap<String,BufferedOutputStream> map = null;
    public Connection(Socket client,HashMap<String,BufferedOutputStream> map) {
        this.client = client;
        this.map = map;
    }

    /**
     * This method runs in a separate thread.
     */
    public void run() {
        try {
            handler.process(client,map);
        }
        catch (java.io.IOException ioe) {
            System.err.println(ioe);
        }
    }
}



