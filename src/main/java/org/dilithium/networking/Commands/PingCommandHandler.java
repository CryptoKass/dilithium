package org.dilithium.networking.Commands;

import org.dilithium.util.ByteArrayKey;

public class PingCommandHandler extends NetworkCommand{

	
	@Override
	public byte[] execute(ByteArrayKey args) {
		if(args.data[0].equals((byte)0xFF));
		return null;
	}

	@Override
	public byte[] recieve(ByteArrayKey args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] send(ByteArrayKey args) {
		// TODO Auto-generated method stub
		return null;
	}

}
