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
    private Context context;
    private Block genesisBlock;
    private Wallet minerWallet;
    private Miner miner;
    private Axiom axiom;
    private BlockHeader tallestHeader;
    private Block currentBlock;
    private boolean shouldMine;
    private boolean isRunning;
    private Thread miningThread;
    private Peer2Peer p2p;
    private final int DEFAULT_PORT = 8888;
    
    public Node(Context context, Block genesisBlock, Miner miner, Axiom axiom){
        this.context = context;
        this.genesisBlock = genesisBlock;
        this.miner = miner;
        this.minerWallet = Start.localWallet;
        this.axiom = axiom;
        this.tallestHeader = genesisBlock.header;
        context.putBlock(genesisBlock);
    }
    
    public Node(){
        this(Start.localContext,GenesisBlock.getInstance().getBlock(), null, NodeSettings.getDefault().getAxiom() );
    }
    
    //getters
    public long getLength(){
        return this.context.calculateChainSize()+1;
    }
    
    public BlockHeader getTallestHeader(){
        return this.tallestHeader;
    }
    
    public boolean isMining(){
        return false;
    }
    
    public Context getContext(){
        return this.context;
    }
    
    //Methods
    public void start(){
        if(miningThread == null){
            miningThread = new Thread(this);
        }
        
        if(p2p == null) {
        		p2p = new Peer2Peer(DEFAULT_PORT);
        }
        
        if(isRunning){
            Commander.CommanderPrint("Node Already running");
            return;
        }
        
        p2p.start();
        shouldMine = true;
        miningThread.start();
        isRunning = false;
    }
    
    public void stop() throws IOException{
        if(miningThread == null){
            Commander.CommanderPrint("Node is not running.");
        }
        
        Commander.CommanderPrint("Stoppping node");
        
        //Stop the miner:
        synchronized(this){
            isRunning = false;
            shouldMine = false;
            this.miner.forceStop();
        }
        //kill the thread
        p2p.stop();
        miningThread.interrupt();
        miningThread = null;
    }
    
    
    private void mine(){
        isRunning = true;
        while(shouldMine){
            
            //if no current block exists - create a new one
            if(currentBlock == null){
                Commander.CommanderPrint("- Creating new block");
                currentBlock = new Block(tallestHeader, axiom);
                Commander.CommanderPrint("- Encoding new block");
                currentBlock.getEncoded();
                Commander.CommanderPrint("- Done");
            }
            
            //Setup Miner
            Commander.CommanderPrint("# Mining current block...");
            this.miner = new Miner(currentBlock);
            this.miner.setAxiom(axiom);
            
            //Begin mining the block
            Block minedBlock = this.miner.mineBlock();
            if(minedBlock == null){
                Commander.CommanderPrint(" Mining interupted !!!");
                break;
            }
            
            //block has been mined
            Commander.CommanderPrint("# Newly mined block is valid:" + axiom.isBlockValid(minedBlock, context));
            Commander.CommanderPrint("+ Adding new block to context...");
            
            //Add mined block to db:
            context.putBlock(minedBlock);
            
            //Update tallest header:
            tallestHeader = minedBlock.header;
            
            //Reset current Block
            currentBlock = null;
            Commander.CommanderPrint("- Complete");
            
        }
    }
    
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
