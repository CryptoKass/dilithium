package org.dilithium.network;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.dilithium.crypto.ecdsa.ECIESCoder;
import org.dilithium.crypto.ecdsa.ECKey;
import org.dilithium.network.messages.Message;
import org.dilithium.util.Tuple;
import org.dilithium.util.serialization.RLP;
import org.dilithium.util.serialization.RLPElement;
import org.dilithium.util.serialization.RLPItem;
import org.dilithium.util.serialization.RLPList;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

import static org.dilithium.network.Peer2Peer.commands;
import static org.dilithium.network.Peer2Peer.waitList;
import static org.dilithium.util.ByteUtil.ONE_BYTE;
import static org.dilithium.util.ByteUtil.ZERO_BYTE;
import static org.dilithium.util.NetUtil.deblobify;
import static org.dilithium.util.NetUtil.semiblobify;

public class Peer extends Thread {
    private byte[] rlpEncoded;
    private boolean parsed;

    private byte[] address;
    private ECPoint pubkey;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private boolean running;
    private boolean initialized;
    private long firstSeen;

    private byte magicbyte = 0x00;

    public Peer(byte[] rlpEncoded) {
        rlpParse();

        try {
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());

            this.firstSeen = System.currentTimeMillis();

            this.initialized = true;
        } catch (Exception e) {
            this.initialized = false;
        }


    }

    public Peer(Socket socket) {
        this(null, socket);
    }

    public Peer(byte[] address, Socket socket) {
        try {
            this.address = address;
            this.socket = socket;

            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());

            this.firstSeen = System.currentTimeMillis();

            this.initialized = true;
        } catch (Exception e) {
            this.initialized = false;
        }
    }

    public boolean hasAddress() {
        return (address != null);
    }

    public byte[] getAddress() {
        return address;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            /* Store received message in memory */
            Message received = receive(in);

            /* Attempt to retrieve a signature from
             * the received message. */
            try {
                received.getSig();
            } catch (Exception e) {
                received = null;
            }

            /* Provided a message was received and
             * that the message was of valid
             * construction, process the message */
            if (received != null) {
                /* If this peer does not have a confirmed
                 * address already associated with it,
                 * retrieve the address from the message
                 * signature and use it to identify this
                 * peer */
                if (!this.hasAddress()) {
                    this.address = received.getSender();
                    this.pubkey = received.getPubKey();
                }

                if(this.hasAddress()) {
                    /* Check the message to make sure it is
                     * from the peer it is believed to be from
                     * (hopefully mitigates man-in-the-middle
                     * attacks */
                    if (Arrays.equals(this.address, received.getSender())) {
                        send(serve(received));
                    }
                }
            }
        }
    }

    public Tuple<Integer, byte[]> serve(Message in) {
        /* Handling join request */
        if (in.getMessageType() == 0x00) {
            /* If the buckets already contain this peer
             * respond with an affirmative */
            if (Peer2Peer.peers.contains(this)) {
                return new Tuple(0x02, ZERO_BYTE);
            } else {
                /* Else attempt to add this peer to the
                 * buckets */
                if (Peer2Peer.peers.add(this)) {
                    /* If successful, remove this peer from
                     * the waitlist and respond with an
                     * affirmative */
                    Peer2Peer.waitList.remove(this);
                    return new Tuple(0x02, ZERO_BYTE);
                } else {
                    /* If adding the peer is unsuccessful,
                     * that means the appropriate bucket is
                     * full. As such, remove this peer from
                     * the waitlist and return a negative. */
                    Peer2Peer.waitList.remove(this);
                    return new Tuple(0x03, ZERO_BYTE);
                }
            }
        }

        /* Handling yes messages */
        if (in.getMessageType() == 0x02) {
            if (Arrays.equals(in.getPayload(), ZERO_BYTE)) {
                if (Peer2Peer.peers.add(this)) {
                    Peer2Peer.waitList.remove(this);
                    return new Tuple(0x02, ONE_BYTE);
                } else {
                    if (!Peer2Peer.peers.contains(this)) {
                        return new Tuple(0x03, ZERO_BYTE);
                    } else {
                        Peer2Peer.waitList.remove(this);
                        return new Tuple(0x02, ONE_BYTE);
                    }
                }
            }
        }

        /* Handling no messages */
        if (in.getMessageType() == 0x03) {
            Peer2Peer.waitList.remove(this);
            Peer2Peer.peers.remove(this);
            this.interrupt();
        }

        /* Handling leave request */
        if (in.getMessageType() == 0x01) {
            Peer2Peer.peers.remove(this);
            waitList.remove(this);
            this.interrupt();
        }

        /* Handling peer request */
        if (in.getMessageType() == 0x05) {
            return new Tuple(0x04, Peer2Peer.peers.serialize());
        }

        /* Handling peerlist */
        if (in.getMessageType() == 0x04) {
            try {
                ArrayList<Peer> potentialList = new ArrayList<>();

                RLPList decodedMessageList = RLP.decode2(in.getPayload());
                RLPList peerlist = (RLPList) decodedMessageList.get(0);

                for (RLPElement rlpElement : peerlist) {
                    if (!(rlpElement instanceof RLPItem)) throw new RuntimeException("Peerlist RLP elements shouldn't be lists");
                }

                for (int i = 0; i < peerlist.size(); i++) {
                    potentialList.add(new Peer(peerlist.get(i).getRLPData()));
                }

                SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");

                Peer2Peer.connect(potentialList.get(rand.nextInt() % potentialList.size()).socket.getInetAddress().toString());
            } catch (Exception e) {

            }
        }



        if (commands.containsKey(in.getMessageType())) {
            commands.get(in.getMessageType()).handle(in);
        }

        return null;
    }

    public Message receive(DataInputStream in) {
        try {
            byte[] chunk;

            ArrayList<byte[]> blob = new ArrayList<>();

            int count;
            do {
                chunk = new byte[32];
                count = in.read(chunk);

                if (count != 32) {
                    return null;
                }

                blob.add(chunk);
            } while (chunk[0] == 0);

            byte[] data = deblobify(blob);

            try {
                Message received = new Message(data);

                received.rlpParse();

                return bloomCheck(data);
            } catch (Exception e) {
                try {
                    byte[] decryptedData = ECIESCoder.decrypt(Peer2Peer.key.getPrivKey(), data);

                    Message received = new Message(decryptedData);

                    received.rlpParse();

                    return bloomCheck(decryptedData);
                } catch (Exception i) {
                    return  null;
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    public void send(int messagetype, byte[] data) {
        this.send(messagetype, ZERO_BYTE, data);
    }

    public void send(Tuple<Integer, byte[]> data) {
        this.send(data.x, ZERO_BYTE, data.y);
    }

    public void send(int messagetype, byte[] target, byte[] data) {
        this.sendTargetted(new Tuple(messagetype, new Tuple(target, data)));
    }

    public void sendTargetted(Tuple<Integer, Tuple<byte[], byte[]>> s) {
        try {
            int i = -1;
            byte[] target = null;
            byte[] data = null;

            if (s != null) {
                i = s.x;
                target = s.y.x;
                data = s.y.y;
            }

            if (data != null && i != -1 && target != null) {
                magicbyte += 0x01;
                Message u = new Message(magicbyte, target, i, data, Peer2Peer.key.getPrivKeyBytes());

                byte[] message = u.getEncoded();

                byte[] toSend;

                if (this.hasAddress()) {
                    byte[] encryptedMessage = ECIESCoder.encrypt(pubkey, message);
                    toSend = semiblobify(encryptedMessage);
                }  else {
                    toSend = semiblobify(message);
                }

                out.write(toSend.length);
                out.write(toSend);
                out.flush();
            }
        } catch (IOException e) {
            this.running = false;
            Peer2Peer.waitList.remove(this);
            Peer2Peer.peers.remove(this);
            this.interrupt();
        }
    }

    public boolean toDelete() {
        if ((System.currentTimeMillis() - this.firstSeen) > 1000) {
            if (!hasAddress()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public String toString() {
        String s = "";
        s = s + Hex.toHexString(address) + ": ";
        s = s + socket.getInetAddress().toString() + ":" + socket.getPort() + "\n";

        return s;
    }

    public Message bloomCheck(byte[] data) {
        if (Peer2Peer.loopHandler.mightContain(data)) {
            return null;
        } else {
            Peer2Peer.loopHandler.put(data);
            Peer2Peer.itemsInserted.getAndIncrement();
            return new Message(data);
        }
    }

    public synchronized void rlpParse() {
        if (parsed) return;
        try {
            RLPList decodedMessageList = RLP.decode2(rlpEncoded);
            RLPList peer = (RLPList) decodedMessageList.get(0);

            if (peer.size() > 3) throw new RuntimeException("Too many RLP elements");
            for (RLPElement rlpElement : peer) {
                if (!(rlpElement instanceof RLPItem)) throw new RuntimeException("Message RLP elements shouldn't be lists");
            }

            this.address = peer.get(0).getRLPData();
            this.pubkey = ECKey.fromPublicOnly(peer.get(1).getRLPData()).getPubKeyPoint();
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress(InetAddress.getByAddress(peer.get(2).getRLPData()), Peer2Peer.port), 3000);

            this.parsed = true;
        } catch (Exception e) {
            throw new RuntimeException("Error on parsing RLP", e);
        }
    }

    public byte[] getEncoded() {
        if (rlpEncoded != null) return rlpEncoded;

        byte[] address = this.address;
        byte[] pubkey = ECKey.fromPublicOnly(this.pubkey).getPubKey();
        byte[] ip = socket.getInetAddress().getAddress();

        this.rlpEncoded = RLP.encodeList(address, pubkey, ip);

        return rlpEncoded;
    }
}
