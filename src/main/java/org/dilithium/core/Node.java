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

import org.dilithium.Start;
import org.dilithium.cli.Commander;
import org.dilithium.config.NodeSettings;
import org.dilithium.core.axiom.Axiom;
import org.dilithium.core.genesis.GenesisBlock;
import org.dilithium.db.Context;
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
    private Thread thread;
    
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
        if(thread == null){
            thread = new Thread(this);
        }
        
        if(isRunning){
            Commander.CommanderPrint("Node Already running");
            return;
        }
               
        shouldMine = true;
        thread.start();
        isRunning = false;
    }
    
    public void stop(){
        if(thread == null){
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
        thread.interrupt();
        thread = null;
    }
    
    
    private void mine(){
        isRunning = true;
        while(shouldMine){
        
            if(currentBlock == null){
                Commander.CommanderPrint("- Creating new block");
                currentBlock = new Block(tallestHeader, axiom);
                Commander.CommanderPrint("- Encoding new block");
                currentBlock.getEncoded();
                Commander.CommanderPrint("- Done");
            }

            Commander.CommanderPrint("# Mining current block...");
            this.miner = new Miner(currentBlock);
            this.miner.setAxiom(axiom);
            Block minedBlock = this.miner.mineBlock();
            if(minedBlock == null){
                Commander.CommanderPrint(" Mining interupted !!!");
                break;
            }
            Commander.CommanderPrint("# Newly mined block is valid:" + axiom.isBlockValid(minedBlock, context));
            Commander.CommanderPrint("+ Adding new block to context...");
            context.putBlock(minedBlock);
            tallestHeader = minedBlock.header;
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
