package org.dilithium.network.peerSet;

import org.dilithium.network.Peer;

import java.util.ArrayList;

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

    public void broadcast(int messagetype, byte[] message) {
        for( int i = 0; i < k; i++) {
            if(peers[i] != null) {
                peers[i].send(messagetype, message);
            }
        }
    }

    public void relay(int messagetype, byte[] target, byte[] message) {
        for( int i = 0; i < k; i++) {
            if(peers[i] != null) {
                peers[i].send(messagetype, target, message);
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

    public boolean contains(Peer p) {
        for (int i = 0; i < k; i++) {
            if (peers[i] == p) {
                return true;
            }
        }

        return false;
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

            if (!peers[i].isRunning()) {
                return i;
            }
        }

        return -1;
    }

    public String toString() {
        String s = "";
        for(int i = 0; i < k; i++) {
            if(peers[i] != null) {
                s = s + peers[i].toString();
            }
        }

        return s;
    }

    public ArrayList<byte[]> serialize() {
        ArrayList<byte[]> peerlist = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            if (peers[i] != null) {
                peerlist.add(peers[i].getEncoded());
            }
        }

        return peerlist;
    }

    protected Peer getRandom(int seed) {
        Peer temp = null;

        while (temp == null) {
            int target = seed % k;
            if (peers[target] != null) {
                temp = peers[target];
            } else {
                seed++;
            }
        }

        return temp;
    }

    protected boolean hasPeers() {
        for (int i = 0; i < k; i++) {
            if (peers[i] != null) {
                return true;
            }
        }

        return false;
    }

    protected int getPeerCount() {
        int peercount = 0;

        for (int i = 0; i < k; i++) {
            if (peers[i] != null) {
                peercount++;
            }
        }

        return peercount;
    }
}
