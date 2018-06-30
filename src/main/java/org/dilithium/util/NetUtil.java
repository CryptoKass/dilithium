package org.dilithium.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.stream.IntStream;

import static org.dilithium.util.ByteUtil.*;

public class NetUtil {
    private static int targetedPacketDataSize = 344;
    private static int untargetedPacketDataSize = 378;

    private static byte[] startByte = {(byte)0xFF};
    private static byte[] zero = {(byte)0x00};

    public static BitSet fromByteArray(byte[] bytes) {
        BitSet bits = new BitSet();
        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
                bits.set(i);
            }
        }
        return bits;
    }

    public static String fromBitSet(BitSet set) {
        final StringBuilder buffer = new StringBuilder(set.length());
        IntStream.range(0, set.length()).mapToObj(i -> set.get(i) ? '1' : '0').forEach(buffer::append);
        String temp = buffer.reverse().toString();
        while(temp.length() < 256) {
            temp = 0 + temp;
        }

        return temp;
    }

    private static byte[] removePadding(byte[] data) {
        while(data[0] == (byte) 0x00) {
            data = Arrays.copyOfRange(data, 1, data.length);
        }

        if(data[0] == startByte[0]) {
            data = Arrays.copyOfRange(data, 1, data.length);
        }

        return data;
    }

    public static int packetSize(int initialSize, byte[] payload) {
        return (initialSize - 211) - ((int)Math.floor(log(16, payload.length) - 2));
    }

    private static double log(int base, int num) {
        double numerator = Math.log(num);
        double denominator = Math.log(base);
        return (numerator / denominator);
    }

    public static String calcDist(byte[] from, byte[] to) {
        return fromBitSet(fromByteArray(xor(from, to)));
    }

    public static ArrayList<byte[]> blobify(byte[] in) {
        int chunkCount = (int)Math.ceil((double)in.length / (double)31);

        ArrayList<byte[]> blob = new ArrayList<byte[]>();
        int processed = 0;
        for (int i = 0; i < chunkCount; i++) {
            byte[] chunk = new byte[32];

            for (int j = 0; j < 32; j++) {
                if (j == 0) {
                    if (in.length - 31 > i * 31) {
                        chunk[j] = (byte)0x00;
                    } else {
                        byte count = (byte)((byte)(in.length - processed) & (byte)0xFF);
                        chunk[j] = count;
                    }
                } else {
                    if (processed < in.length) {
                        chunk[j] = in[processed];
                        processed++;
                    } else {
                        chunk[j] = (byte)0x00;
                    }
                }
            }

            blob.add(chunk);
        }

        return blob;
    }

    public static byte[] deblobify(ArrayList<byte[]> in) {
        byte[] temp = new byte[0];

        for (int i = 0; i < in.size(); i++) {
            byte[] current = in.get(i);

            if (current[0] == 0x00) {
                temp = concat(temp, org.bouncycastle.util.Arrays.copyOfRange(current, 1, 32));
            } else {
                int dist = current[0];
                temp = concat(temp, org.bouncycastle.util.Arrays.copyOfRange(current, 1, dist + 1));
            }
        }

        return temp;
    }

    public static byte[] semiblobify(byte[] in) {
        ArrayList<byte[]> temp = blobify(in);

        byte[] tempBytes = new byte[0];

        for (int i = 0; i < temp.size(); i++) {
            tempBytes = concat(tempBytes, temp.get(i));
        }

        return tempBytes;
    }

    public static ArrayList<byte[]> semideblobify(byte[] in) {
        byte[][] temp = partition(in, 32);
        ArrayList<byte[]> tempList = new ArrayList<>();

        for (int i = 0; i < temp.length; i++) {
            tempList.add(temp[i]);
        }

        return tempList;
    }
}
