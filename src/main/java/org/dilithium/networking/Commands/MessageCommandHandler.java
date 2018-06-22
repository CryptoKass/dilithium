package org.dilithium.networking.Commands;

import org.dilithium.util.ByteArrayKey;
import org.dilithium.util.ByteUtil;

public class MessageCommandHandler extends NetworkCommand {
    public byte[] recieve(ByteArrayKey data) {
        try {
            String s = new String(data.toByteArray(), "UTF-8");
            System.out.println(s);
        } catch (Exception e) {
            System.out.println("Failed to print an incoming string.");
        }

        return null;
    }

    public byte[] send(byte[] data) {
        return ByteUtil.concatenateBytes(new ByteArrayKey((byte) 0x00, (byte) 0x01).toByteArray(), data);
    }

    private void check(byte[] data) {

    }

    @Override
    public byte[] handle(ByteArrayKey args) {
        if(new ByteArrayKey(args.toByteArray()[1]).equals(new ByteArrayKey((byte) 0x00))) {
            System.out.println("Sending Transaction");
            return send(args.subSet(2, args.toByteArray().length-1));
        } else if(new ByteArrayKey(args.toByteArray()[1]).equals(new ByteArrayKey((byte) 0x01))) {
            System.out.println("Recieved Transaction");
            return recieve(new ByteArrayKey(args.subSet(2, args.toByteArray().length-1)));
        } else if(new ByteArrayKey(args.toByteArray()[1]).equals(new ByteArrayKey((byte) 0x02))) {
            check(args.subSet(2, args.toByteArray().length-1));
        }

        return null;
    }
}
