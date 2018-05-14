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
import org.dilithium.Start;
import org.dilithium.core.axiom.Axiom;
import org.dilithium.core.axiom.AxiomManager;
import org.dilithium.serialization.ParcelData;
import org.dilithium.serialization.Serializer;
import org.dilithium.util.ByteUtil;
import org.dilithium.util.Encoding;
import org.dilithium.util.JsonUtil;

/**
 * This class 
 */
public class BlockHeader { // ~141 bytes
    
    /* The hash of this block */
    private byte[] hash; // 32 bytes
    
    /* The Hash of the parent block */
    private byte[] parentHash; // 32 bytes
    
    /* The root hash of the merkle Tree containing all transactions in this block */
    private byte[] merkleRoot; // 32 bytes 
    
    /* A Data message for this block, this can and should be left empty, unless genisis block or new ruleset */
    private byte[] blockData; // <32 bytes 
    
    /* A reasonable timestamp for this block */
    private byte[] timestamp;
    
    /* The difficulty this block is mined at calculated by the parent blocks difficulty and the timestamp */
    private long difficulty; // ~4 bytes
    
    /* Cached target bytes based on the difficulty */
    private byte[] target;
    
    /* The index number of this block in the chain, equal to the number of blocks before it. */
    private long index; // ~4 bytes 
    
    /* The nonce used to solve the hash cash problem */
    private byte[] nonce; // ~4 bytes 
    
    /* The byte code ID for a given ruleset (axiom), default ruleset is {0b00000000 0b00000000} - base-ruleset.
     * The Axiom is a ruleset that tells the node processing this block how to handle it.
     * Rulesets may change in the event of a fork or an update. Forexample one axiom may require a certain hashing algorithm. */
    private byte[] axiomID; // 2 bytes .
    
    /* The monolithium(Mono) value of the reward sent to the miner */
    private BigInteger reward;
    
    /* rewarded miner address */
    private byte[] minerAddress;
        
    /* Data to be used by axiom, should it be needed */
    private byte[] axiomData;
    
    /* This is not offically part of the block header */
    private Axiom axiom;
    
    //Constructors:
    public BlockHeader(byte[] hash, byte[] parentHash, byte[] blockData, byte[] timestamp , long difficulty, long index, byte[] nonce, byte[] axiomID, BigInteger reward, byte[] minerAddress, byte[] axiomData ) {
        this.hash = hash;
        this.parentHash = parentHash;
        this.blockData = blockData;
        this.timestamp = timestamp;
        this.difficulty = difficulty;
        this.index = index;
        this.nonce = nonce;
        this.axiomID = axiomID;
        this.reward = reward;
        this.minerAddress = minerAddress;
        this.axiomData = axiomData;
        this.merkleRoot = new byte[] {0};
    }
    
    public BlockHeader(byte[] parentHash, byte[] blockData, long index, byte[] axiomID){
        this.parentHash = parentHash;
        this.blockData = blockData;
        this.index = index;
        this.axiomID = axiomID;
        this.merkleRoot = new byte[] {0};        
    }
    
    public BlockHeader(BlockHeader parent, Axiom axiom){
        this.hash = new byte[] {0};
        this.parentHash = parent.getHash();
        this.blockData = new byte[] {0};
        this.timestamp = ByteUtil.getNowTimeStamp();
        this.difficulty = parent.getDifficulty();
        this.index = parent.getIndex() + 1;
        this.nonce = ByteUtil.intToBytes(0);
        this.axiomID = axiom.getAxiomID();
        this.reward = parent.getReward();
        this.minerAddress = Start.localWallet.getAddress();
        this.axiomData = new byte[] {0};
        this.merkleRoot = new byte[] {0};
    }
    
    public BlockHeader(byte[] parcel){
        ParcelData[] parcelData = Serializer.getParcelData(parcel);
        this.hash = parcelData[0].getData();
        this.parentHash = parcelData[1].getData();
        this.blockData = parcelData[2].getData();
        this.merkleRoot = parcelData[3].getData();
        this.timestamp = parcelData[4].getData();
        this.difficulty = ByteUtil.bytesToBigInteger(parcelData[5].getData()).longValue();
        this.index = ByteUtil.bytesToBigInteger(parcelData[6].getData()).longValue();
        this.nonce = parcelData[7].getData();
        this.reward = ByteUtil.bytesToBigInteger(parcelData[8].getData());
        this.axiomID = parcelData[9].getData();
        this.minerAddress = parcelData[10].getData();
        this.axiomData = parcelData[11].getData();
    }
    
    //getters:
    public byte[] getHash(){
        return this.hash;
    }
    
    public byte[] getParentHash(){
        return this.parentHash;
    }
    
    public byte[] getMerkleRoot(){
        return this.merkleRoot;
    }
    
    public long getDifficulty(){

        this.difficulty = getAxiom().calculateDifficulty(this);
        return this.difficulty;
    }
    
    public long getIndex(){
        return this.index;
    }
    
    public byte[] getAxiomData(){
        return this.axiomData;
    }
    
    public byte[] getBlockData(){
        return this.blockData;
    }
    
    public byte[] getNonce(){
        return this.nonce;
    }
    
    public byte[] getTimeStamp(){
        return this.timestamp;
    }
    
    /* Gets a parcel encoded version of this block ready to be hashed,
     * This method can be over ridden by the axiom.
     * However eventually it may be beneficial to cache this result
     * and append the nonce.
    */
    public byte[] getHashData(){
        byte[] hashData = Serializer.createParcel(
                new Object[]{
                        getParentHash(),
                        getNonce(),
                        getTimeStamp(),
                        BigInteger.valueOf(getIndex()), //Parcel encoding doesnt support longs
                        BigInteger.valueOf(getDifficulty()), //Parcel encoding doesnt support longs
                        getMinerAddress(),
                        getReward(),
                        getMerkleRoot(),
                        getAxiomData(),
                        getBlockData()
                });
        return hashData;
    }
    
    public void setNonce( byte[] nonce ){
        this.nonce = nonce;
    }
    
    public byte[] setBlockHash( byte[] blockHash ){
        this.hash = blockHash;
        return this.hash;
    }
    
    public byte[] setMerkleRoot( byte[] newRoot){
        this.merkleRoot = newRoot;
        return merkleRoot;
    }
    
    public byte[] setAxiomData( byte[] axiomData){
        return this.axiomData = axiomData;
    }
    
    public byte[] getAxiomID(){
        return this.axiomID;
    }
    
    public Axiom getAxiom(){
        return AxiomManager.getAxiom(getAxiomID());
    }
    
    public BigInteger getReward(){
        return this.reward;
    }
    
    public byte[] getMinerAddress(){
        return this.minerAddress;
    }
    
    public byte[] getEncoded(){
        byte[] parcel = Serializer.createParcel(new Object[]{
            this.hash,              //0
            this.parentHash,        //1
            this.blockData,         //2
            this.merkleRoot,        //3
            this.timestamp,         //4
            BigInteger.valueOf(this.difficulty), //5
            BigInteger.valueOf(this.index), //6
            this.nonce,             //7
            this.reward,            //8
            this.axiomID,           //9
            this.minerAddress,      //10
            this.axiomData          //11
        });
              
        //System.out.println("header parcel length: " + parcel.length);
        return parcel;
    }
    public String getJson(){
        return JsonUtil.getJson(this);
    }
    @Override
    public String toString(){
        return "block-header: {\n" +
                "- hash: " + Encoding.bytesToHex(getHash()) + ", \n" +
                "- parent-hash: " + Encoding.bytesToHex(getParentHash()) + ", \n" +
                "- merkle-root: " + Encoding.bytesToHex(getMerkleRoot()) + ", \n" +
                "- nonce: " + ByteUtil.bytesToInt(nonce) + ", \n" +
                "- block-reward: " + reward + ", \n" +
                "- minerAddress: " + Encoding.bytesToHex(minerAddress) + " \n" +
                "- }";
        
    }
}
