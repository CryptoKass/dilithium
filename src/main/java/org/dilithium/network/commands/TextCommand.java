package org.dilithium.network.commands;

import org.bouncycastle.util.encoders.Hex;
import org.dilithium.network.messages.Message;
import org.dilithium.util.Tuple;

public class TextCommand extends NetworkCommand {
    @Override
    public Tuple<Integer, byte[]> handle(Message in) {
        try {
            System.out.println(Hex.toHexString(in.getSender()) + ": " + new String(in.getPayload(), "UTF-8"));
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
