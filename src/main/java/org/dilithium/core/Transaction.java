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

import org.bouncycastle.util.encoders.Hex;
import org.dilithium.core.axiom.Axiom;
import org.dilithium.serialization.ParcelData;
import org.dilithium.serialization.Serializer;
import org.dilithium.util.ByteUtil;
import org.dilithium.util.Encoding;
import org.dilithium.util.HashUtil;
import org.dilithium.util.ecdsa.ECKey;

/**
 * This class 
 */
public class Transaction {
    
    //Transaction contents:
    
    /* The hash of the parcel encoded transactiions contents: nonce, value, data, receipient, sender and networkId */
    private byte[] hash;
    /* this nonce prevents replay attacks */
    private final byte[] nonce;
    /* the value in monolithium(mono); */
    private final byte[] value;
    /* the data sent along with transaction, this can also contain methods calls for smart contracts */
    private final byte[] data;
    /* the recipient of the value sent, this can be either a personal account of a contract account */
    private final byte[] recipient;
    /* network id prevents replay attack by specifying the transactions network e.g. main-net, test-net or a forked network */
    private final byte networkId;
    /* this transaction signed by the sender, with separate values for r, s and v */ 
    private final byte[] r;
    private final byte[] s;
    private final byte v;
    
    /* the senders(aka the owner of the dilithium being sent) public key. (this can be salvaged from signature) */
    private final byte[] sender; //Note sender should be recovered from signature, and will eventually no longer be part of the tx hash.
    /* parcel encoded transaction */
    private byte[] encoded;
    /* this verified bool is true if transaction is actually from the owner of the balance -> signature is valid */
    private boolean verified;
  
    
    // Constructors :
    public Transaction(byte[] privateKey, BigInteger nonce, BigInteger value, byte[] data, byte[] recipient, byte networkId, byte[] sender, Axiom axiom){
        this(privateKey, ByteUtil.bigIntegerToBytes(nonce),ByteUtil.bigIntegerToBytes(value), data, recipient, networkId, sender, axiom);
    }
    
    public Transaction(byte[] privateKey, byte[] nonce, byte[] value, byte[] data, byte[] recipient, byte networkId, byte[] sender, Axiom axiom){
        this.nonce = nonce;
        this.value = value;
        this.data = data;
        this.recipient = recipient;
        this.networkId = networkId;
        this.sender = sender;

        ECKey.ECDSASignature temp = generateSignature(privateKey, axiom);
        this.r = temp.r.toByteArray();
        this.s = temp.s.toByteArray();
        this.v = temp.v;

        this.hash = getHash();
    }
    
    public Transaction(byte[] nonce, byte[] value, byte[] data, byte[] recipient, byte networkId, byte[] r, byte[] s, byte v, byte[] sender){
        this.nonce = nonce;
        this.value = value;
        this.data = data;
        this.recipient = recipient;
        this.networkId = networkId;
        this.r = r;
        this.s = s;
        this.v = v;
        this.sender = sender;
        this.hash = getHash();
    }
    
    public Transaction(byte[] parcel){
        this(Serializer.getParcelData(parcel), parcel);
    }
    
    public Transaction(ParcelData[] parcelData, byte[] parcel){
        //this.nonce, this.value, this.data, this.recipient, this.networkId, this.sender
        this.nonce = parcelData[0].getData();
        this.value = parcelData[1].getData();
        this.data = parcelData[2].getData();
        this.recipient = parcelData[3].getData();
        this.networkId = parcelData[4].getData()[0];
        this.r = parcelData[5].getData();
        this.s = parcelData[6].getData();
        this.v = parcelData[7].getData()[0];
        this.sender = parcelData[8].getData();
        this.hash = getHash();
        this.encoded = parcel;
    }
    
    // Methods :
    public byte[] getEncoded(){
        if(encoded == null){
            encoded = Serializer.createParcel(new Object[]{this.nonce, this.value, this.data, this.recipient, this.networkId, this.r, this.s, this.v, this.sender});
        }
        return encoded;
    }
    
    /* Verify the transactions Signature using an axiom: */
    public boolean verifySignature(Axiom axiom){
        //use axiom to verify signature.
        verified = axiom.verifySignature(this);
        return verified;
    }
    
    /* quickly check if the transaction is verified */
    public boolean isVerified(){
        return verified;
    }
    
    /* getter for the transaction hash, if none exists then one will be generated */
    public byte[] getHash(){
        //Todo This should use an axiom.
        if(this.hash == null){
            this.hash = HashUtil.applySha256(getParcelled());
        }
        return hash;
    }

    public byte[] getParcelled() {
        return Serializer.createParcel(new Object[]{  this.nonce, this.value, this.data, this.recipient, this.networkId, this.r, this.s, this.v, this.sender});
    }

    public byte[] getParcelledSansSig() {
        return Serializer.createParcel(new Object[]{  this.nonce, this.value, this.data, this.recipient, this.networkId, this.sender});
    }
    
    /* generate signature using an axiom */
    public ECKey.ECDSASignature generateSignature(byte[] privateKey, Axiom axiom){
        return axiom.generateSignature(this, privateKey);
    }
    
    public byte[] getSender(){
        return this.sender;
    }
    
    public byte[] getR() {
        return r;
    }
    
    public byte[] getS() {
        return s;
    }
    
    public byte getV() {
        return v;
    }
    
    public byte[] getNonce(){
        return this.nonce;
    }
    
    public byte[] getData(){
        return this.getData();
    }
    
    public byte[] getRecipient(){
        return this.recipient;
    }
    
    public byte networkId(){
        return this.networkId;
    }
    
    public byte[] getValue(){
        return this.value;
    }
    
    @Override
    public String toString(){
        return "transaction: {\n" +
                "- sender: " + Hex.toHexString(sender) + ", \n" +
                "- recipient: " + Encoding.bytesToAddress(getRecipient()) + ", \n" +
                "- value: " + ByteUtil.bytesToBigInteger(getValue()) + ", \n" +
                "- R: " + Hex.toHexString(r) + ", \n" +
                "- S: " + Hex.toHexString(s) + ", \n" +
                "- V: " + Encoding.byteToHex(v) + ", \n" +
                "- }";
        
    }
    
}
