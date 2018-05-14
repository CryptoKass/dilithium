
import java.io.File;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Arrays;
import org.bouncycastle.asn1.ASN1Primitive;
import org.dilithium.core.AccountState;
import org.dilithium.core.Transaction;
import org.dilithium.db.Storage;
import org.dilithium.serialization.ParcelData;
import org.dilithium.serialization.Serializer;
import static org.dilithium.test.Testing.totalTestsRun;
import org.dilithium.util.ByteUtil;
import org.dilithium.util.Encoding;
import org.dilithium.util.HashUtil;

/* 
 * Copyright (C) 2018 Dilithium Team .
 *
 * The Dilithium library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

/**
 * This class 
 */
public class ignore {
    /*
}
       public void testKeyPair(int num) {
        totalTestsRun++;
        System.out.println("\n------------------------------------------------------------------------");
        System.out.println("#"+ totalTestsRun +" Testing key Pairs:");
        System.out.println("------------------------------------------------------------------------");
        for(int i=0;i < num; i++){
            System.out.println("\n -- ["+(i+1)+" KEY PAIR] --");
            //generate key pair:
            KeyPair keys = KeyUtil.GenerateKeyPair();
            //converting key pairs to string:
            String priv = KeyUtil.privateKeyToString((ECPrivateKey) keys.getPrivate());
            String pub = KeyUtil.publicKeyToString((ECPublicKey) keys.getPublic());
            //converting string back to keys:
            ECPrivateKey privkey = KeyUtil.stringToPrivateKey(priv);
            ECPublicKey pubkey = KeyUtil.stringToPublicKey(pub);
            //converting key to address:
            String address = KeyUtil.publicKeyToAddress(pubkey);
            
            System.out.println( "Raw-Private-Key: " + KeyUtil.privateKeyToString((ECPrivateKey) keys.getPrivate()));
            System.out.println( "Raw-Public-Key:  " + KeyUtil.publicKeyToString((ECPublicKey) keys.getPublic()));
            System.out.println( "Address:         " + KeyUtil.publicKeyToAddress(pubkey));
        }  
        System.out.println("\n [[Key Pairs working successfully]]");
	}
    
    public void testDBConnections(){
        totalTestsRun++;
        System.out.println("\n------------------------------------------------------------------------");
        System.out.println("#"+ totalTestsRun +" Testing Database Connections:");
        System.out.println("------------------------------------------------------------------------\n");
        
        System.out.println("Opening dbs...");
        Storage.getInstance();
        System.out.println("Commiting to dbs...");
        Storage.getInstance().commitAll();
        System.out.println("close All Db connections...");
        Storage.getInstance().closeAll();
        System.out.println("Working directory:");
        System.out.println(new File(".").getAbsolutePath());
        System.out.println("\n [[Db successfully working]]");
    }
    
    public void testHashingAlogrithms(){
        totalTestsRun++;
        System.out.println("\n------------------------------------------------------------------------");
        System.out.println("#"+ totalTestsRun +" Testing Hashing Algorithms:");
        System.out.println("------------------------------------------------------------------------\n");
        
        byte[] input = ByteUtil.stringToBytes("hello");
        byte[] inputWithSha256 = HashUtil.applySha256(input);
        String inputaddress = Encoding.bytesToAddress(inputWithSha256, true);
        System.out.println("hash as address:" + inputaddress);
        System.out.println("\n [[Hashing Algorithms working successfully]]");
    }
    
    public void testParceling(){
        totalTestsRun++;
        System.out.println("\n------------------------------------------------------------------------");
        System.out.println("#"+ totalTestsRun +" Testing serialization:");
        System.out.println("------------------------------------------------------------------------\n");
        byte[] parcel = Serializer.createParcel(new Object[]{"number of bytes required to represent this BigInteger, including at least one sign bit, which iswwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwO12",300});
        System.out.println("Object serialized...");
        System.out.println("Deserializing Parcel...");
        System.out.println("Deserialized Parcel: " +Arrays.toString(Serializer.getParcelData(parcel)));
        
        System.out.println("\n [[Parcelling working as expected]]");
    }
    
    public void testAccountState(){
        totalTestsRun++;
        System.out.println("\n------------------------------------------------------------------------");
        System.out.println("#"+ totalTestsRun +" Testing Account State: ");
        System.out.println("------------------------------------------------------------------------\n");
        
        AccountState account = new AccountState(100,1,new byte[]{});
        System.out.println(account.toString());
        System.out.println("Getting encoded state parcel...");
        byte[] state = account.getEncoded();
        System.out.println("Deserializing parcel...");
        ParcelData[] stateParcel = Serializer.getParcelData(state);
        //System.out.println(Arrays.toString(stateParcel));
        System.out.println("Recreating Account State object from parcel...");
        System.out.println(new AccountState(state));
        
        System.out.println("\n [[Account State working successfully]]");
    }
    
    public void testTransactions(){
        totalTestsRun++;
        System.out.println("\n------------------------------------------------------------------------");
        System.out.println("#"+ totalTestsRun +" Testing Transactions: ");
        System.out.println("------------------------------------------------------------------------\n");
        
        System.out.println("Generating Keys... ");
        KeyPair keys = KeyUtil.GenerateKeyPair();
        byte[] publicKey = KeyUtil.encodeECPublicKey((ECPublicKey) keys.getPublic());
        ECPrivateKey privateKey = (ECPrivateKey) keys.getPrivate();
        //public Transaction(ECPrivateKey privateKey, BigInteger nonce, BigInteger value, byte[] data, byte[] recipient, byte networkId, byte[] sender){
        System.out.println("Creating new transaction...");
        
        Transaction tx = new Transaction(privateKey, BigInteger.TEN, BigInteger.TEN, new byte[0], ByteUtil.stringToBytes("bob"), (byte) 0, publicKey);
        System.out.println("Verifying transaction signature");
        System.out.println("Is-Signature-valid: " + tx.verifySignature(this.getTestAxiom()));
        
        System.out.println("\n [[Transactions Signed and verified successfully]]");
    }
    
    public void testPubKeyRecovery(){
        
        totalTestsRun++;
        System.out.println("\n------------------------------------------------------------------------");
        System.out.println("#"+ totalTestsRun +" Testing public key recovery: ");
        System.out.println("------------------------------------------------------------------------\n");
            
        KeyPair keys = KeyUtil.GenerateKeyPair();
        byte[] publicKey = KeyUtil.encodeECPublicKey((ECPublicKey) keys.getPublic());
        ECPrivateKey privateKey = (ECPrivateKey) keys.getPrivate();
        Transaction tx = new Transaction(privateKey, BigInteger.TEN, BigInteger.TEN, new byte[0], ByteUtil.stringToBytes("bob"), (byte) 0, publicKey);
        try {
            byte[] sig = tx.getSignature();
            byte[] hash = tx.getHash();
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify((ECPublicKey) keys.getPublic());
            ecdsaVerify.update(tx.getHash());
            boolean verified = ecdsaVerify.verify(tx.getSignature());
            System.out.println("signature length: " + sig.length);
           
            ASN1Primitive decodedSig = ByteUtil.toAsn1Object(sig).toASN1Primitive();
            ECPublicKey ecpubkey = (ECPublicKey) keys.getPublic();
            ecpubkey.getW().getAffineY();
            //v = 27 + (y % 2)
            int v = 27 + (ecpubkey.getW().getAffineY().mod(BigInteger.valueOf(2))).intValue() ;
                        

            /*int b1 = sig[3];
            int rstart = 4;
            int rend = b1 + rstart;
            
            int b2 = sig[rend+1];
            int sstart = rend + 2;
            int send = b2 + sstart;
            
            BigInteger r = ByteUtil.bytesToBigInteger(Arrays.copyOfRange(sig, rstart, rend));
            BigInteger s = ByteUtil.bytesToBigInteger(Arrays.copyOfRange(sig, sstart, send));
            System.out.println("signature: " + decodedSig );
            System.out.println("signature-b1: " + (int) b1 );
            System.out.println("signature-b2: " + (int) b2 );
            System.out.println("signature-r: " + r.toString() );
            System.out.println("signature-s: " + s.toString() );
            System.out.println("signature-v: " + v );    
            
            
        } catch (Exception e) {
            System.out.println("failed");
        }
        */
}
