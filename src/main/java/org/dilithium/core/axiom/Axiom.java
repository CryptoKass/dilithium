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
package org.dilithium.core.axiom;

import java.math.BigInteger;
import java.security.PrivateKey;
import org.dilithium.core.Block;
import org.dilithium.core.BlockHeader;
import org.dilithium.core.Transaction;
import org.dilithium.db.Context;
import org.dilithium.util.ecdsa.ECKey;

/*
 * The axiom is a set of rules and methods, for generating and validating blocks and transactions.
 * This method allows dynamic flexibility amoung how the blockchain works, providing a
 * course of action to advances or changes in the security of hashing functions or difficulty,
 * while still providing legacy support to old blocks.
 * Axioms can also be used in customized private network dilithium chains, to setup custom behaviour,
 * and targeted security.
 * Axiom - premise or starting point for further reasoning.
 * 
 * TODO: 
 * Add more axiom methods to allow more customization.
*/
public interface Axiom {
    
    public byte[] getAxiomID(); // 2 byte maximum
    public boolean requireBlockUpdate();
    public boolean requireTransactonUpdate();
        
    public byte[] applyBlockHash( BlockHeader header );
    public byte[] generateBlockSolution( BlockHeader header );
    public long calculateDifficulty( BlockHeader header );
    public BigInteger getBlockReward(BlockHeader headers);
    public BlockHeader updateBlock( BlockHeader header );
       
    
    public Transaction updateTransction(Transaction tx);
    public boolean verifyTransaction(Transaction tx, Context context);
    public boolean verifySignature(Transaction tx);
    public ECKey.ECDSASignature generateSignature(Transaction tx, byte[] privateKey);
    public String toString();
    public boolean isBlockSolutionValid(BlockHeader header);
    public boolean isBlockValid(Block block, Context context);
}
