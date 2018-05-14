/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dilithium.core;

import java.math.BigInteger;
import org.dilithium.core.axiom.Axiom;
import org.dilithium.db.StorageContext;
import org.dilithium.util.Log;
import java.util.logging.Level;
import org.dilithium.util.ByteArrayKey;
import org.dilithium.util.ByteUtil;
import org.dilithium.util.KeyUtil;

/**
 * This class Reads a block or an encodedBlock and processes it;
 * The Block is checked for validity and the transactional state updates are applied (e.g. balance added to accounts)
 */
public class BlockProcessor {
    
    public boolean addBlock(byte[] blockParcel, StorageContext context){
        return addBlock(new Block(blockParcel), context);
    }
    
    public boolean addBlock(Block block, StorageContext context){
        BlockHeader header = block.header;
        
        //get the axiom from the block;
        Axiom axiom = header.getAxiom();
        if(axiom == null){
            Log.log(Level.WARNING, "Failed to add block to chain... Axiom missing");
            return false;
        }
        
        
        //Check if the block is valid using the axiom
        if(!axiom.isBlockValid(block, context)){
            Log.log(Level.WARNING, "The block failed axiom validation");
            return false;
        }
        
        //Add block to context;
        context.putBlock(block);
        
        //Update account states
        int txlength = block.getTransactionLength();
        Transaction tx;
        AccountState accountState;
        
        //Loop through each tx and update the state.
        for(int i=0; i < txlength; i++){
            
            tx = block.getTransaction(i);
            
            //Gather the values from tx.
            BigInteger value = ByteUtil.bytesToBigInteger(tx.getValue());
            BigInteger nonce = ByteUtil.bytesToBigInteger(tx.getNonce());
            ByteArrayKey sender = new ByteArrayKey(tx.getSenderAddress());
            ByteArrayKey reciepient = new ByteArrayKey(tx.getRecipient());
            
            //update recipients account state.
            accountState = context.getAccount(reciepient); /* get the reciepient account from db context */
            accountState = accountState.addBalance(value); /* update the accounts balance */
            context.putAccount(reciepient, accountState);  /* save the new state to db context */
            
            //update senders account state.
            accountState = context.getAccount(sender);          /* get the sender account from db context */
            accountState = accountState.removeBalance(value);   /* update the accounts balance */
            accountState = accountState.setNonce(nonce);        /* update the accounts nonce */
            context.putAccount(sender, accountState);           /* save the new state to db context */
                        
        }
        
        //give reward to miner
        BigInteger reward = axiom.getBlockReward(header);
        ByteArrayKey minerAddress = new ByteArrayKey(header.getMinerAddress());
        accountState = context.getAccount(minerAddress);
        accountState = accountState.addBalance(reward);
        context.putAccount(minerAddress, accountState);
        
        return true;
    }

}
