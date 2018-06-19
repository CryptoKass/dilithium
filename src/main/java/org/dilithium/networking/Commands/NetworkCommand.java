package org.dilithium.networking.Commands;

import org.dilithium.util.ByteArrayKey;

public abstract class NetworkCommand {

    //If leading byte is 0xFF, send, if 0x00 recieve
	public abstract byte[] handle(ByteArrayKey args);
	
}

