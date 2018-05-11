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

package org.dilithium.core;

import java.math.BigInteger;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dilithium.config.NodeSettings;
import org.dilithium.core.axiom.AxiomManager;
import org.dilithium.util.ByteUtil;
import org.dilithium.util.HashUtil;
import org.dilithium.util.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This class 
 */
public class BlockTest {
    
    NodeSettings config;
    
    @Before
    public void setup(){
        /* Setup Config */
        config = NodeSettings.getDefault();
        
        /* Setup bouncey castle as security provider */
        Security.addProvider(new BouncyCastleProvider());
    }
    
    @Test
    public void testBlockEncodingAndReconstruction(){
        byte[] bytes = HashUtil.applySha256(ByteUtil.stringToBytes("testBytes"));

        BlockHeader header = new BlockHeader(bytes, bytes, bytes, bytes, 0L, 0L, bytes, AxiomManager.getAxiom("axiomD0").getAxiomID(), BigInteger.ZERO, bytes, bytes);

        List<byte[]> txs = new ArrayList<byte[]>();
        //    public Transaction(byte[] nonce, byte[] value, byte[] data, byte[] recipient, byte networkId, byte[] signature, byte[] sender)
        txs.add(new Transaction(bytes,bytes,bytes,bytes,bytes[0],bytes, bytes, bytes[0], bytes).getEncoded());

        Block originalBlock = new Block(header,AxiomManager.getAxiom("axiomD0"), txs);
        byte[] encodedBlock = originalBlock.getEncoded();

        Log.log(Level.INFO, "block encoded + " + encodedBlock.length);
        
        Block otherBlock = new Block(encodedBlock);
        byte[] otherEncodedBlock = otherBlock.getEncoded();

        Log.log(Level.INFO, "block encoded + " + encodedBlock.length);
        
        Assert.assertTrue("Block Encoding & Decoding Failed ",Arrays.equals(otherEncodedBlock,encodedBlock));
    }
    
    
}
