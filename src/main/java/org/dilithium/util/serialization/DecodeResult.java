package org.dilithium.util.serialization;

import org.bouncycastle.util.encoders.Hex;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DecodeResult implements Serializable {

    private int pos;
    private Object decoded;

    public DecodeResult(int pos, Object decoded) {
        this.pos = pos;
        this.decoded = decoded;
    }

    public int getPos() {
        return pos;
    }

    public Object getDecoded() {
        return decoded;
    }

    public String toString() {
        return asString(this.decoded);
    }

    private String asString(Object decoded) {
        if (decoded instanceof String) {
            return (String) decoded;
        } else if (decoded instanceof byte[]) {
            return Hex.toHexString((byte[]) decoded);
        } else if (decoded instanceof Object[]) {
            String result = "";
            for (Object item : (Object[]) decoded) {
                result += asString(item);
            }
            return result;
        }
        throw new RuntimeException("Not a valid type. Should not occur");
    }
}