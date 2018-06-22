/*
 * Copyright (C) 2018 Dilithium Team .
 *
 * The Dilithium library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package org.dilithium.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dilithium.Start;
import org.dilithium.networking.Commands.NetworkCommand;
import org.dilithium.networking.Commands.PingCommandHandler;
import org.dilithium.util.ByteArrayKey;
import org.dilithium.util.Log;

public class Peer {
	
    private Thread peerThread;
    private byte[] address;
    public Socket socket;
    private static HashMap<ByteArrayKey, NetworkCommand> commands = new HashMap<>();
    private DataOutputStream out;
    private DataInputStream in;
    private boolean runningServer;
    
    public Peer(Socket socket)  {
		this.socket = socket;
		try {
			this.out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        initializeCommands();
		peerThread = new Thread(new Runnable() {
			public void run() {
                try {
                    listen();
                    System.out.println("Closing connection to " + socket.getInetAddress() + ":" + socket.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });	
		start();
	}

	public byte[] getAddress() {
        return address;
    }

	public void start(){
        if(peerThread.isAlive()){
            System.out.println("Peer Thread is already running.");
            return;
        }
        runningServer = true;
        peerThread.start();
    }
	
	public void stop() throws IOException{
		runningServer = false;
		try {
			peerThread.interrupt();
			socket.close();
	    } catch (NullPointerException n) {
	    		n.printStackTrace();
	    }
	    System.out.println("Peer Closed");
	}

	private void initializeCommands() {
		/**List of Commands
		 * 0xFF - Ping
		 * 0x00 - Transaction
		 */
		this.commands.put(new ByteArrayKey((byte)0xFF), new PingCommandHandler()); 
    }
	
	public void listen() throws IOException {
		byte[] command;
		DataInputStream in = new DataInputStream(socket.getInputStream());
		System.out.println("Listening to socket :" + socket.toString());
		while(runningServer){
	    		try{
        			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        			command = receive(in);
        			send(serve(command), out);
	    		} catch (SocketTimeoutException e) {
	    			e.printStackTrace();
	    		}
        

		}
	}
	
	public byte[] serve(byte[] input) {
		//In execute, send the args as input without the first byte
        return commands.get(new ByteArrayKey(input[0])).handle(new ByteArrayKey(input));
    }

	public void send(byte[] data, DataOutputStream out){
        try {
        		if(data != null) {
	        		out.writeInt(data.length);
	            out.write(data);
	            out.flush();
        		}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] receive(DataInputStream in){
		byte[] data = null;
		int size = 0;
		try {
        		size = in.readInt();
        		data = new byte[size];
        		in.readFully(data, 0, size);
            System.out.println("Received message " + size);
        } catch (IOException e) {
            e.printStackTrace();
        }
		return data;
    }

    @Override
    public String toString() {
        return String.format("[%s:%s]", socket.getInetAddress(), socket.getPort());
    }
}
