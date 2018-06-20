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

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Queue;

import org.dilithium.Start;
import org.dilithium.cli.Commander;
import org.dilithium.config.NodeSettings;
import org.dilithium.core.axiom.Axiom;
import org.dilithium.core.genesis.GenesisBlock;
import org.dilithium.db.Context;
import org.dilithium.networking.Peer2Peer;
import org.dilithium.util.Encoding;

/**
 * This Class handles the block-chain as a full-node.
 */
public class Node implements Runnable{
    /* The storage and db context used by this node */
    private Context context;
    
    /* The first block in the blockchain, via context */
    private Block genesisBlock;
    
    /* The wallet that will recieve rewards for each block mined (If the current axiom supports that */ 
    private Wallet minerWallet;
    
    /* The Miner class handles PoW mining */
    private Miner miner;
    
    /* The current axiom in use by the latest block in the blockchain */
    private Axiom axiom;
    
    /* The header from the latest block in the blockchain, via context. */
    private BlockHeader tallestHeader;
    
    /* The block that is being mined */
    private Block currentBlock;
    
    /* Node and mining status variables: */
    private boolean shouldMine;
    private Thread miningThread;
    
    /* This nodes peer to peer variables */
    private static Peer2Peer p2p;
    private int serverPort = 8888;
    
    /* Checks block and updates the state and adds it to the db context */
    private BlockProcessor blockProcessor;
    
    /* Transactions to be added to block */
    private Queue<Transaction> transactionPool;
    
    //Contructor 
    public Node(Context context, Block genesisBlock, Miner miner, Axiom axiom){
        this.context = context;
        this.genesisBlock = genesisBlock;
        this.miner = miner;
        this.minerWallet = Start.localWallet;
        this.axiom = axiom;
        this.tallestHeader = genesisBlock.header;
        this.blockProcessor = new BlockProcessor();
        blockProcessor.addBlock(genesisBlock, context);
        transactionPool = new PriorityQueue<Transaction>();
    }
    
    public Node(){
        this(Start.localContext,GenesisBlock.getInstance().getBlock(), null, NodeSettings.getDefault().getAxiom() );
    }
    
    //getters
    public long getLength(){
        return this.context.calculateChainSize();
    }
    
    public int getPort(){
        return this.serverPort;
    }
    
    public BlockHeader getTallestHeader(){
        return this.tallestHeader;
    }
    
    public boolean isMining(){
        return this.shouldMine;
    }
    
    public Context getContext(){
        return this.context;
    }
    
    //Methods
    public void addTransactionToPool(Transaction tx){
        transactionPool.add(tx);
    }
    
    public void start(){
        if(miningThread == null){
            miningThread = new Thread(this);
        }
        
        if(p2p == null) {
        		p2p = new Peer2Peer(serverPort);
        }
        
        if(shouldMine){
            Commander.CommanderPrint("Node Already running");
            return;
        }
        
        p2p.start();
        shouldMine = true;
        miningThread.start();
    }
    
    public void stop() throws IOException{
        if(miningThread == null){
            Commander.CommanderPrint("Node is not running.");
        }
        
        Commander.CommanderPrint("Stoppping node");
        
        //Stop the miner:
        synchronized(this){
            shouldMine = false;
            this.miner.forceStop();
        }
        //kill the thread
        p2p.stop();
        miningThread.interrupt();
        miningThread = null;
    }
        
    private void mine(){
        while(shouldMine){
            
            //if no current block exists - create a new one
            if(currentBlock == null){
                Commander.CommanderPrint("Creating new block");
                currentBlock = new Block(tallestHeader, axiom);
                //add transactions to block from the transaction pool
                while(!transactionPool.isEmpty()){
                    currentBlock.addTransaction(transactionPool.remove());
                }
                Commander.CommanderPrint("Encoding new block");
                currentBlock.getEncoded();
                Commander.CommanderPrint("Done");
            }
            
            //Setup Miner
            Commander.CommanderPrint("Mining current block...");
            this.miner = new Miner(currentBlock);
            this.miner.setAxiom(axiom);
            
            //Begin mining the block
            Block minedBlock = this.miner.mineBlock();
            if(minedBlock == null){
                Commander.CommanderPrint("Mining interupted !!!");
                break;
            }
            
            //block has been mined
            Commander.CommanderPrint("Newly mined block is valid:" + axiom.isBlockValid(minedBlock, context));
            Commander.CommanderPrint("Adding new block to context...");
            
            //Add mined block to db:
            //context.putBlock(minedBlock);
            blockProcessor.addBlock(minedBlock, context);
            
            //Update tallest header:
            tallestHeader = minedBlock.header;
            
            //Reset current Block
            currentBlock = null;
            Commander.CommanderPrint("Complete");
            
        }
    }
    
    public void setPort(int number){
        this.serverPort = serverPort;
    }
    
    public AccountState getAccount(byte[] address){
        return context.getAccount(address);
    }
    
    public 
    
    //Overrides
    @Override
    public String toString(){
        return "node: {\n" +
                "- chain-length: " + getLength() + ", \n" +
                "- latest-hash: " + Encoding.bytesToHex(getTallestHeader().getHash()) + ", \n" +
                "- is-node-mining: " + isMining() + ", \n" +
                "- }";
    }

    @Override
    public void run() {
        mine();
    }
    
    
}
