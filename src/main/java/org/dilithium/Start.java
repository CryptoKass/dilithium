package org.dilithium;

import org.bouncycastle.util.encoders.Hex;
import org.dilithium.application.MainChat;
import org.dilithium.crypto.ecdsa.ECKey;
import org.dilithium.network.Peer2Peer;
import org.dilithium.network.messages.Message;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Scanner;
import java.util.stream.IntStream;

import static org.bouncycastle.pqc.math.linearalgebra.ByteUtils.xor;
import static org.dilithium.util.ByteUtil.EMPTY_BYTE_ARRAY;
import static org.dilithium.util.ByteUtil.ZERO_BYTE;

public class Start {
    public static void main(String[] args) throws Exception {
        try {
            ECKey key = new ECKey();
            System.out.println(Hex.toHexString(key.getAddress()));
            Peer2Peer net = new Peer2Peer(40424, key, 6, 10);
            net.start();
            
            MainChat.main(null);
            
            Scanner s = new Scanner(System.in);

            boolean running = true;

            System.out.println("System initialized.");
            System.out.println("To sendTargetted a message, enter '1'.\n" +
                    "To connect to a peer, enter '2'. \n" +
                    "To list currently connected peers, enter '3'.\n" +
                    "To check how many peers are in the waitlist, enter '4'.\n" +
                    "To exit the program, enter '5'.\n" +
                    "To spam your peers, enter '6'.");

            while (running) {
                int choice = s.nextInt();
                s.nextLine();
                if(choice == 1) {
                    System.out.print("Enter the message you'd like to sendTargetted: ");
                    String sending = s.nextLine();
                    net.broadcast(0xF0, sending.getBytes("UTF-8"));
                } else if(choice == 2) {
                    System.out.print("Enter IP: ");
                    String ip = s.next();

                    System.out.print("Enter port: ");
                    int port = s.nextInt();

                    net.connect(ip, port);
                } else if(choice == 3) {
                    System.out.println("Current Peers: ");
                    System.out.println(net.getPeers());
                } else if(choice == 4) {
                    System.out.println("Waitlist Count: ");
                    System.out.println(Peer2Peer.waitList.size());
                } else if(choice == 5) {
                    System.out.println("Shutting down.");
                    running = false;
                } else if(choice == 6) {
                    System.out.print("Enter the number of messages to sendTargetted: ");
                    int count = s.nextInt();
                    for (int i = 0; i < count; i++) {
                        SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
                        byte[] data = new byte[50];
                        rand.nextBytes(data);
                        net.broadcast(0xF4, data);
                    }
                    System.out.println("Sent!");
                }

                System.out.println();
            }

            System.exit(666);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed.");
        }
    }
}
