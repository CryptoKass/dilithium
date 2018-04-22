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

package org.dilithium.core.genesis;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.dilithium.Start;
import org.dilithium.core.Block;
import org.dilithium.core.BlockHeader;
import org.dilithium.core.Transaction;
import org.dilithium.core.Wallet;
import org.dilithium.core.axiom.AxiomManager;
import org.dilithium.util.ByteUtil;
import org.dilithium.util.HashUtil;

/**
 * 
 */
public class GenesisBlock implements GenesisConfig {
    
    private final BlockHeader header;
    private final Block block;
    private final byte[] hash;
    private final byte[] blockData;
    private static GenesisBlock instance;
    private Wallet wallet;
    
    public GenesisBlock(){
        //byte[] hash, byte[] parentHash, byte[] blockData, byte[] timestamp , long difficulty, long index, byte[] nonce, byte[] axiomID, BigInteger reward, byte[] minerAddress, byte[] axiomData 
        header = new BlockHeader(
                getHash(),
                getHash(),
                getBlockData(),
                ByteUtil.getNowTimeStamp(),
                3,
                0,
                ByteUtil.intToBytes(0),
                AxiomManager.getAxiom("axiomD0").getAxiomID(),
                BigInteger.ZERO,
                getHash(),
                getHash()
        );
        List<byte[]> txs = new ArrayList<byte[]>();
        Transaction tx = Start.localWallet.generateTransaction(BigInteger.ONE, getHash(), (byte) 0, Start.localContext, AxiomManager.getAxiom("axiomD0"));
        txs.add( tx.getEncoded() );
        
        block = new Block(header, AxiomManager.getAxiom("axiomD0"), txs);
        hash = header.getHash();
        blockData = header.getBlockData();
    }

    @Override
    public BlockHeader getBlockHeader() {
        return this.header;
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    @Override
    public byte[] getHash() {
        return HashUtil.applySha256(ByteUtil.stringToBytes("kass"));
    }
    
    @Override
    public byte[] getBlockData() {
        return ByteUtil.stringToBytes("dilithium rocks");
    }
    
    public static GenesisBlock getInstance(){
        if(instance == null){
            instance = new GenesisBlock();
        }
        return instance;
    }
    
    
}
