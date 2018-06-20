package org.dilithium.core;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dilithium.networking.Peer2Peer;
import org.dilithium.util.ecdsa.ECKey;
import org.junit.Before;
import org.junit.Test;

import java.net.Socket;
import java.security.Security;
import java.util.Scanner;

public class NetworkTest {
    ECKey key = new ECKey();
    Peer2Peer connection = new Peer2Peer(40424, key.getAddress());

    Scanner s = new Scanner(System.in);

    @Before
    public void setup() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testP2PMessaging() throws Exception {
        connection.start();

        boolean running = true;

        while (running) {
            switch (s.nextInt()) {
                case 1: sendMessage();
                        break;
                case 2: listPeers();
                        break;
                case 3: addPeer();
                        break;
                case 4: running = false;
                        break;
            }
        }
    }

    public void sendMessage() throws Exception {
        byte[] toSend = s.nextLine().getBytes("UTF-8");
        Peer2Peer.propagate(toSend);
    }

    public void listPeers() {
        System.out.println("Listing connected peers: ");
        connection.listPeers();
    }

    public void addPeer() throws Exception {
        System.out.print("Enter an IP: ");
        String ip = s.next();
        System.out.print("Enter a port: " );
        int port = s.nextInt();
        connection.connect(new Socket(ip, port));
    }

}
