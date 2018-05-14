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
import org.dilithium.db.Context;
import org.dilithium.util.ByteUtil;
import org.dilithium.util.ecdsa.ECKey;

/**
 * This class 
 */
public class Wallet {
    private ECKey keyPair;
    private Axiom axiom;
    
    public byte[] getPublicKey(){
        return this.keyPair.getPubKey();
    }
    
    public byte[] getAddress(){
        return this.keyPair.getAddress();
    }
    
    @Override
    public String toString(){
        return "wallet: { \n" +
                "public-key: " + Hex.toHexString(this.keyPair.getPubKey()) + ", \n" +
                "address: " + Hex.toHexString(this.keyPair.getAddress()) +" \n" +
                "}";
    }
    
    public String toString(Context context){
        return "wallet: { \n" +
                "public-key: " + Hex.toHexString(this.keyPair.getPubKey()) + ", \n" +
                "address: " + Hex.toHexString(this.keyPair.getAddress()) +" \n" +
                "balance: " + getBalance(context) + " \n" +
                "}";
    }
    
    
    public Wallet(){
        this.keyPair = new ECKey();
    }
    
    public Wallet(byte[] privKey){
        this.keyPair = ECKey.fromPrivate(privKey);
    }
    
    /* returns balance as it is recorded in the given context */
    public BigInteger getBalance(Context context){
        AccountState account = context.getAccount(keyPair.getAddress());
        if(account != null) {
            return account.getBalance();
        }else{
            return BigInteger.ZERO;
        }
    }
    
    /*generates a transaction, without doing any validity checks */
    public Transaction generateTransaction(BigInteger value, byte[] recipient, byte networkId, Context context, Axiom axiom){
        
        byte[] nonce = ByteUtil.bigIntegerToBytes(BigInteger.ZERO);
        byte[] data = ByteUtil.intToBytes(0);
        byte[] sender = keyPair.getAddress();
        
        //attempt to get account nonce;
        AccountState account = context.getAccount(keyPair.getAddress());
        if(account != null){
            nonce = ByteUtil.bigIntegerToBytes(account.getNonce());
            nonce = ByteUtil.increment(nonce);
        }
       
        Transaction tx = new Transaction(keyPair.getPrivKeyBytes(), nonce, ByteUtil.bigIntegerToBytes(value), data, recipient, networkId, sender, axiom);
        //Transaction(ECPrivateKey privateKey, byte[] nonce, byte[] value, byte[] data, byte[] recipient, byte networkId, byte[] sender, Axiom axiom)
        
        return tx;
    }
    
}
