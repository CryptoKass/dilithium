package org.dilithium.networking.peerSet;

import org.dilithium.networking.Peer;

import java.io.DataOutputStream;
import java.util.BitSet;
import java.util.stream.IntStream;

import static org.bouncycastle.pqc.math.linearalgebra.ByteUtils.xor;

public class PeerSet {
    private int k;
    private Bucket[] buckets = new Bucket[256];
    private byte[] nodeAddress;

    public PeerSet(byte[] nodeAddress, int k) {
        this.nodeAddress = nodeAddress;
        this.k = k;
    }

    public boolean add(Peer p) {
        int bucketIndex = calcDist(nodeAddress, p.getAddress()).indexOf('1');

        if(bucketIndex >= 0) {
            if(buckets[bucketIndex] == null) {
                buckets[bucketIndex] = new Bucket(k, nodeAddress);
            }

            return buckets[bucketIndex].add(p);
        } else {
            return false;
        }
    }

    public boolean remove(Peer p) {
        int bucketIndex = calcDist(nodeAddress, p.getAddress()).indexOf('1');

        if(bucketIndex >= 0) {
            if(buckets[bucketIndex] == null) {
                return false;
            }

            return buckets[bucketIndex].remove(p);
        } else {
            return false;
        }
    }

    public void broadcast(byte[] message, DataOutputStream out) {
        for(int i = 0; i < 256; i++) {
            if(buckets[i] != null) {
                buckets[i].broadcast(message, out);
            }
        }
    }

    public static String calcDist(byte[] from, byte[] to) {
        return fromBitSet(fromByteArray(xor(from, to)));
    }

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
}
