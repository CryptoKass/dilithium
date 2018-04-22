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

import java.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.dilithium.core.axiom.Axiom;
import org.dilithium.core.axiom.AxiomManager;
import org.dilithium.db.Context;
import org.dilithium.util.ByteUtil;

/**
 * This class 
 */
public class Miner {
    
    public Context context;
    
    private Block block;
    
    private Axiom axiom;
    
    private boolean shouldMine = false;
    
    //Constructor:
    public Miner(Block block){
        this.block = block;
        axiom = AxiomManager.getAxiom(block.header.getAxiomID());
    }
    
    public Block mineBlock(){
        shouldMine = true;
        BlockHeader header = block.header;
        //Get difficulty
        long difficulty = header.getDifficulty();
        
        //generate a target set of bytes from:
        byte[] target = new byte[(int) difficulty];
        target = ByteUtil.populate(target, (byte) 0);
        
        //cache for solution
        byte[] solution;
        
        System.out.println("begining mining"); //TODO replace this with a logger
        boolean solved = false;
        
        while(!solved && shouldMine){
            solution = axiom.generateBlockSolution(header);
            //System.out.println("// nonce: " +  ByteUtil.bytesToInt(header.getNonce()) );
            //System.out.println("// Target:" + Hex.toHexString(target));
            //System.out.println("// attempt: " + Hex.toHexString(Arrays.copyOfRange(solution, 0, target.length)));
            solved = Arrays.equals(Arrays.copyOfRange(solution, 0, target.length), target);
        }
        System.out.println("- successfully mined!");
        System.out.println("- solution: " + Hex.toHexString(header.getHash()));
        
        if(!shouldMine) return null;
        return block;
    }
    
    public void setAxiom(Axiom axiom){
        this.axiom = axiom;
    }
    
    public void forceStop(){
        shouldMine = false;
    }
}
