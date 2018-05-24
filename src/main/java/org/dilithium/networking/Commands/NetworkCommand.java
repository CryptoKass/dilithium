package org.dilithium.networking.Commands;

import org.dilithium.util.ByteArrayKey;

public abstract class NetworkCommand {
	
	//Receives Data and Determines how to process
	public abstract byte[] execute(ByteArrayKey args);

	//Process Recieved Data
	public abstract byte[] recieve(ByteArrayKey args);
	
	//Send Requested Data
	public abstract byte[] send(ByteArrayKey args);
	
}

