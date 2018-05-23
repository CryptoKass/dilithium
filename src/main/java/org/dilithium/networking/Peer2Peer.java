package org.dilithium.networking;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;


import org.dilithium.networking.Commands.NetworkCommand;
import org.dilithium.networking.Commands.PingCommandHandler;
import org.dilithium.util.ByteArrayKey;

public class Peer2Peer {

	private int port;
    private ArrayList<Peer>  peers;
    private DataOutputStream out;
    private Thread           serverThread;
    private boolean          runningServer;
    private HashMap<ByteArrayKey, NetworkCommand> commands = new HashMap<>();
    private ServerSocket server;
    private Socket socket;
    
    //Node with out storing Blockchain
    public Peer2Peer(int port){
	System.out.println("Making node");
    this.port = port;
    peers = new ArrayList<>();
    serverThread = new Thread(new Runnable() {
        public void run() {
            try {
                listen();
                System.out.println("Connection Ended");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });		
    initializeCommands();
}
    
    private void initializeCommands() {
    		/**List of Commands
    		 * 0xFF - Ping
    		 * 0xFE - Block_Height
    		 */
        this.commands.put(new ByteArrayKey((byte)0xFF), new PingCommandHandler()); 
    }

    public void start(){
        if(serverThread.isAlive()){
            System.out.println("Server is already running.");
            return;
        }
        runningServer = true;
        serverThread.start();
    }

    public void stop() throws IOException{
    		runningServer = false;
    		try {
        		serverThread.interrupt();
    			socket.close();
        } catch (NullPointerException n) {
        		n.printStackTrace();
        }
        System.out.println("Server Stopped");
    }

    public void listen() throws IOException, SocketTimeoutException{
        System.out.println("Server starting...");
        server = new ServerSocket(this.port);
        System.out.println("Server started on port " + this.port);
        	
        Peer peer;
        server.setSoTimeout(10000);
        while(runningServer){
        		//System.out.println("Waiting for a connection");
        		try{
        			socket = server.accept();
                System.out.println("Passed Accept");
                peer = new Peer(socket);
                System.out.println("Connection received from: " + peer.toString());
                peers.add(peer);
                System.out.println("New peer: " + peer.toString());
        		} catch (SocketTimeoutException e) {
        			//e.printStackTrace();
        		}
            

        }
    }

    public void connect(Socket socket){
        try {
            out = new DataOutputStream(socket.getOutputStream());
            Peer.send(commands.get(new ByteArrayKey((byte) 0xFF)).execute(new ByteArrayKey((byte) 0xFF)), out);		
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
}