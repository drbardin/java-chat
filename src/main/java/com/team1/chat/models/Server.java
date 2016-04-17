package com.team1.chat.models;


import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/*
    Class to implement the server-side functionality and main method for a chat service
 */
public class Server
{
    private DatabaseSupport dbs;
    private HashMap<String, ArrayList<ClientThread>> channelRosters;
    private int port;
    private int numClients = 0;
    private boolean running;

    /*
        Constructor
     */
    public Server(int port)
    {
        this.port = port;
        dbs = new DatabaseSupport();
        channelRosters = new HashMap<String, ArrayList<ClientThread>>();
    }

    /*
        Activate client thread
     */
    public void start()
    {
        running = true;
        try {
            // initialize server's socket
            ServerSocket serverSocket = new ServerSocket(port);

            // listen for connections
            while (running) {

                System.out.println("Server listening for clients on port: " + port);

                // someone is trying to connect. accecpt the conn.
                Socket socket = serverSocket.accept();
                
                if (!running)
                    break;

                // add thread to master-list and send them to initialize.
                ClientThread ct = new ClientThread(socket);
                addToChannelRoster(ct);
                System.out.println("Client #" + (numClients++) + ": " + ct.username + " has been connected.");
                ct.start();
            }

            // stop running
            try {
                serverSocket.close();

                // iterate map and terminate each thread
                for (ArrayList<ClientThread> mapValueArr : channelRosters.values()) {
                    for (ClientThread cls : mapValueArr) {
                        try {
                            cls.socketInput.close();
                            cls.socketOutput.close();
                            cls.socket.close();
                        } catch (IOException e) {
                            System.out.println("Error during shutdown: " + e);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception closing the server and clients: " + e);
            }
        } catch (IOException e) {
            System.out.println("Critical error creating new server socket: " + e);
        }
        // remove all key-value pairs from the channelRoster.
        channelRosters.clear();
    }
    /*
     * On login or channel change, method is called to check for existence of
     * channel key, if not there create a new key->ArrayList pair.
     * Then place thread object into the proper channel.
     */
    public synchronized void addToChannelRoster(ClientThread ct){

        if (!channelRosters.containsKey(ct.channel)) {
            channelRosters.put(ct.channel, new ArrayList<ClientThread>());
        }
        channelRosters.get(ct.channel).add(ct);

    }

    /*
     * when user sends message containing "/joinChannel <channel>" message is intercepted by
     *  ClientThread run() and triggers channel change
     */
    public synchronized void changeChannel(ClientThread ct, String newChannel){

        int index = channelRosters.get(ct.channel).indexOf(ct);
        if(index == -1){
            System.out.println("Change channel failed. Client thread not found in channel roster.");
            return;
        }
        ClientThread mover = channelRosters.get(ct.channel).remove(index);
        mover.channel = newChannel;

        addToChannelRoster(mover);
    }

    /*
     * Use channel key to retrieve roster for channel X,
     * Loop key-value arraylist and broadcast message from end of list to beginning
     * If message send fails, disconnect the user that failed.
     */
    public synchronized void broadcast(String message, String channel)
    {	
    	if (!channel.isEmpty()){
	        // Get the roster for the channel
	        ArrayList<ClientThread> clients = channelRosters.get(channel);
	
	        // print to server console
	        System.out.println("Channel: " + channel + " - " + message);
	
	        // print to clients, loop backwards so if we have to remove, we don't go OoB or skip a user.
	        for (int i = clients.size()-1; i >= 0; --i) {
	
	            // if sendMessage for a client fails, disconnect them from channel.
	            if (!clients.get(i).sendMessage(message)) {
	
	                System.out.println("Client: " + clients.get(i).username + " disconnected. Removing from active.");
	                removeFromServerList(channel, i);
	            }
	        }
    	}
    }

    /*
     * Removes a ClientThread from the channelRoster
     */
    public synchronized void removeFromServerList(String key, int index) {

       channelRosters.get(key).remove(index);
    }

    /*
        MAIN METHOD
     */
    public static void main(String args[])
    {
        if (args.length != 1)
            System.out.println("Usage: java Server < port >");
        else {
            Server server = new Server(Integer.parseInt(args[0]));
            server.start();
        }
    }

    /*
            Internal class for a client thread.

            One instance per client.
     */
    class ClientThread extends Thread
    {
        Socket socket = null;
        ObjectInputStream socketInput;
        ObjectOutputStream socketOutput;
        int thread_ID;
        int numClients;
        String username;
        String channel;
        String message;

        /* Construct a new client thread */
        public ClientThread(Socket socket)
        {
            thread_ID = numClients += 1;
            this.socket = socket;
            // On thread creation, add them to Lobby.
            this.channel = "Lobby";

            // create data streams
            System.out.println("Thread setting up Object I/O streams.");
            try {
                socketOutput = new ObjectOutputStream(socket.getOutputStream());
                socketInput = new ObjectInputStream(socket.getInputStream());
                username = (String) socketInput.readObject();
                System.out.println("Thread connection successful.");
            } catch (IOException e) {
                System.out.println("Error creating new I/O streams");
            } catch (ClassNotFoundException e) {
                System.out.println("Class error " + e);
            }
        }


        /*
             Listener for client in infinite loop.
             Terminates when "logmeoff" read by server during message send
         */
        public void run()
        {
            boolean running = true;
            while (running) {

                // read string
                try {
                    message = (String) socketInput.readObject();
                } catch (IOException e) {
                    System.out.println(thread_ID + " error thrown reading stream. " + e);
                    break;
                } catch (ClassNotFoundException c) {
                    break;
                }
                // *** Check for passed command before broadcasting ***
                // user sent logout message. Set flag, and break loop. Perform logout.
                if(message.contains("/logout")){
                    running = false;
                }
                // user changed screen name. set this.username to  username.
                else if(message.contains("/changeName")){

                    String[] input = message.split(" ");
                    if(!input[1].isEmpty())
                        username = input[1];
                }
                // user wants to switch channels.
                else if(message.contains("/joinChannel")){
                    String[] input = message.split(" ");
                    String newChannel = "";
                    //If there are no arguments, it denotes that the user is only leaving a channel.
                    if (input.length!=1){
                    	//Otherwise, the specified value denotes the new channel.
                    	newChannel = input[1];
                    }
                    	changeChannel(this, newChannel);
                }
                else {
                    // renamed from sendMessage, less ambiguity for server/client methods.
                    // print to server
                    broadcast("["+channel+"]"+"["+username +"]"+": "+ message, channel);
                }
            }
            // user passed "/logout" message. Remove from channel roster, close connections.
            System.out.println("User " + username + " is logging out.");
            int index = channelRosters.get(channel).indexOf(this);
            removeFromServerList(channel, index);
            close();
        }

        public void close()
        {

            try {
                if (socketOutput != null)
                    socketOutput.close();
            } catch (Exception e) {
                System.out.println("Closing output socket threw: " + e);
            }
            try {
                if (socketInput != null)
                    socketInput.close();
            } catch (Exception ec) {
                System.out.println("Closing input socket threw: " + ec);
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception ece) {
                System.out.println("Closing server socket threw: " + ece);
            }
            System.out.println("User " + username + " has been disconnected.");
        }

        public boolean sendMessage(String message)
        {
            // a client is no longer connected, close out their connection.
            if (!socket.isConnected()) {
                close();
                return false;
            }

            try {
                socketOutput.writeObject(message);
            } catch (IOException e) {
                System.out.println("Message failed in route to: " + username);
                System.out.println(e.toString());
            }
            return true;
        }
    }
}