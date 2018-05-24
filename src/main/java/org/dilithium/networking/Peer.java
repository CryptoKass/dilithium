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
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import org.dilithium.networking.Commands.NetworkCommand;
import org.dilithium.util.ByteArrayKey;

public class Peer {
	
    private Thread peerThread;  
    public Socket socket;
    private static HashMap<ByteArrayKey, NetworkCommand> commands = new HashMap<>();
    
	public Peer(Socket socket)  {
		this.socket = socket;
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
		peerThread.start();
	}

	private void initializeCommands() {
		
    }
	
	public void listen() throws IOException {
		byte[] command;
		DataInputStream in = new DataInputStream(socket.getInputStream());
		while(true){
	    		try{
        			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        			command = receive(in);
        			send(serve(command), out);
	    		} catch (SocketTimeoutException e) {
	    			e.printStackTrace();
	    		}
        

		}
	}
	
	public static byte[] serve(byte[] input) {
		//TODO In execute, send the args as input without the first byte
        return commands.get(new ByteArrayKey(input[0])).execute(new ByteArrayKey(input,1,input.length-1));
    }

    public static void send(byte[] data, DataOutputStream out){
        System.out.println("Sending message: " + data);
        try {
        		out.writeInt(data.length);
            out.write(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] receive(DataInputStream in){
		byte[] data = null;
		try {
        		int size = in.readInt();
        		in.readFully(data, 0, size);
            System.out.println("Received message: "+ data);
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
