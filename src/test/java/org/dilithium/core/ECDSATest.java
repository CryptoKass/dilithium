package org.dilithium.core;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.dilithium.util.Log;
import org.dilithium.util.ecdsa.ECKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.security.Security;
import java.util.Arrays;
import java.util.logging.Level;

import static org.dilithium.util.HashUtil.applyKeccak;

public class ECDSATest {

    @Before
    public void setup(){
        /* Setup bouncycastle as security provider */
        Security.addProvider(new BouncyCastleProvider());

    }

    @Test
    public void testKeypairGenerationSigningAndRecovery() {
        /* Generate random keypair */
        ECKey keyPair = new ECKey();

        Log.log(Level.INFO, "Keypair address " + Hex.toHexString(keyPair.getAddress()));

        byte[] privKey = keyPair.getPrivKeyBytes();

        /* Reconstruct keyPair from private key */
        ECKey reconstructedKeyPair = ECKey.fromPrivate(privKey);

        Log.log(Level.INFO, "Keypair Private Key: " + Hex.toHexString(keyPair.getPrivKeyBytes()));

        Log.log(Level.INFO, "Recovered Keypair Address: " + Hex.toHexString(reconstructedKeyPair.getAddress()));

        Log.log(Level.INFO, "Recovered Keypair Private Key: " + Hex.toHexString(reconstructedKeyPair.getPrivKeyBytes()));

        /* String on which to test signing */
        byte[] testString = "TestString".getBytes();

        /* Perform signing */
        ECKey.ECDSASignature sig = keyPair.sign(applyKeccak(testString));

        Log.log(Level.INFO, "Signature: " + Hex.toHexString(sig.toByteArray()));

        /* Retrieve components from signature */
        byte[] r = sig.r.toByteArray();
        byte[] s = sig.s.toByteArray();
        byte v = sig.v;

        /* Reconstruct signature from components */
        ECKey.ECDSASignature reconstructedSig = ECKey.ECDSASignature.fromComponents(r,s,v);

        /* Make sure reconstructed signature is verifiable */
        Assert.assertTrue("Signature validation failed.", ECKey.verifyWithRecovery(testString, reconstructedSig));

        /* Make sure public addresses match */
        Assert.assertTrue("Key reconstruction from private key failed.", Arrays.equals(reconstructedKeyPair.getAddress(), keyPair.getAddress()));
    }
}
