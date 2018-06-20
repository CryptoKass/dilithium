package org.dilithium.networking.peerSet;

import org.dilithium.networking.Peer;

import java.io.DataOutputStream;

public class Bucket {
    byte[] zero = {(byte)0x00};
    private Peer[] peers;
    private int k;
    private byte[] ref;

    Bucket(int k, byte[] ref) {
        this.k = k;
        this.peers = new Peer[k];
        this.ref = ref;
    }

    public void broadcast(byte[] message, DataOutputStream out) {
        for( int i = 0; i < k; i++) {
            if(peers[i] != null) {
                peers[i].send(message, out);
            }
        }
    }

    public boolean add(Peer p) {
        int toBeReplaced = checkNodes();

        if(!(toBeReplaced < 0)) {
            peers[toBeReplaced] = p;
            return true;
        } else {
            /* Could potentially make it prioritize closer nodes within
             * this bucket with the following code */
            /*
            for(int i = 0; i < k; i++) {
                if(BIUtil.isLessThan(new BigInteger(concat(zero, xor(ref, n.getAddress()))), new BigInteger(concat(zero, xor(ref, nodes[i].getAddress()))))) {
                    nodes[i] = n;
                    return true;
                }
            }
             */
            return false;
        }
    }

    public boolean remove(Peer p) {
        for(int i = 0; i < k; i++) {
            if(peers[i] == p) {
                peers[i] = null;
                return true;
            }
        }

        return false;
    }

    private int checkNodes() {
        for(int i = 0; i < k; i++) {
            if(peers[i] == null) {
                return i;
            }
        }

        return -1;
    }
}
