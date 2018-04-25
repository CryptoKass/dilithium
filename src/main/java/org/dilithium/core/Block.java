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

import java.util.ArrayList;
import java.util.List;
import org.dilithium.Start;
import org.dilithium.core.axiom.Axiom;
import org.dilithium.core.axiom.AxiomManager;
import org.dilithium.db.Context;
import org.dilithium.serialization.ParcelData;
import org.dilithium.serialization.Serializer;
import org.dilithium.util.ByteUtil;
import org.dilithium.util.HashUtil;

/**
 * This class 
 */
public class Block {
    
    public BlockHeader header;
    private List<byte[]> transactions;
    private byte[] encoded;
    private boolean parsed = false;
    private byte[] axiomID;
    private Axiom axiom;
    private Context context;
    
    public Block(BlockHeader header, Axiom axiom, List<byte[]> transactions){
        this.header = header;
        this.axiom = axiom;
        this.axiomID = axiom.getAxiomID();
        this.transactions = transactions;
    }
    
    public Block(byte[] parcel){
        ParcelData[] parcelData = Serializer.getParcelData(parcel);
        
        this.header = new BlockHeader(parcelData[0].getData());
        this.axiomID = parcelData[1].getData();
        this.axiom = AxiomManager.getAxiom(axiomID);
        this.transactions = ByteUtil.parcelDataToListBytes(parcelData[2].getData());
    }
    
    /* Create a new Block */
    public Block(BlockHeader parent, Axiom axiom){
        BlockHeader newHeader = new BlockHeader(parent, axiom);
        this.header = newHeader;
        this.axiom = axiom;
        this.transactions = new ArrayList<byte[]>();
        //Setup extra header data:
        this.header.setMerkleRoot(getMerkleRoot());
        this.header.getDifficulty();
        this.context = Start.localContext;
    }
    
    public void addTransaction(Transaction tx){
        if(axiom.verifyTransaction(tx,context)){
            transactions.add(tx.getEncoded());
            header.setMerkleRoot(getMerkleRoot());
        }
    }
    
    public int getTransactionLength(){
        return this.transactions.size();
    }
    
    public Transaction getTransaction(int index){
        byte[] txBytes = transactions.get(index);
        if(txBytes == null ) return null;
        return new Transaction(txBytes);
    }
    
    public byte[] getMerkleRoot(){
        int cursor = 0;
        int nodes = transactions.size();
        byte[] nodeA;
        byte[] nodeB;
        ArrayList<byte[]> layer = new ArrayList<byte[]>();
        ArrayList<byte[]> layerchild = new ArrayList<byte[]>();
        
        //check if transactions exist populated:
        if(nodes == 0){
            return HashUtil.applyBlake2b(new byte[]{0});
        }
        
        //check for single tx
        if(nodes == 1){
            return HashUtil.applyBlake2b(transactions.get(0));
        }
        
        //generate bottom level
        for(int i=0; i < nodes -1; i+= 2){
            nodeA = new Transaction(transactions.get(i)).getHash();
            nodeB = new Transaction(transactions.get(i+1)).getHash();
            layer.add(HashUtil.applyBlake2b(ByteUtil.concatenateBytes(nodeA, nodeB)));
        }
        nodes = layer.size();
        
        //generate tree:
        while(layer.size() > 1){
            cursor = 0;
            while(cursor < layer.size()){
                nodeA = layer.get(cursor);
                cursor++;
                nodeB = layer.get(cursor);
                layerchild.add(HashUtil.applyBlake2b(ByteUtil.concatenateBytes(nodeA, nodeB)));
                cursor++;
            }
            layer = layerchild;
        }
        
        //return the merkle root
        return layer.get(0);
    }
    
    public byte[] getEncoded(){
        byte[] encodedBlock = Serializer.createParcel(new Object[]{
                header.getEncoded(),    //0
                axiom.getAxiomID(),     //1
                transactions            //2
        });
        return encodedBlock;
    }
    
    @Override 
    public String toString(){
        return header.toString();
    }
}
