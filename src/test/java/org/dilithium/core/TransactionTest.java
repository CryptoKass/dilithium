package org.dilithium.core;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.dilithium.core.axiom.AxiomD0;
import org.dilithium.db.Context;
import org.dilithium.util.Log;
import org.dilithium.util.ecdsa.ECKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.security.Security;
import java.util.Arrays;
import java.util.logging.Level;

import static org.dilithium.util.HashUtil.applyKeccak;

public class TransactionTest {
    @Before
    public void setup(){
        /* Setup bouncycastle as security provider */
        Security.addProvider(new BouncyCastleProvider());

    }

    @Test
    public void testTransactionGenerationAndValidation() {
        Wallet wallet = new Wallet();
        Transaction tx = wallet.generateTransaction(BigInteger.TEN, (new ECKey()).getAddress(), (byte)0x00, new Context(), new AxiomD0());

        Assert.assertTrue("Transaction signature validated correctly.", tx.verifySignature(new AxiomD0()));

    }
}
