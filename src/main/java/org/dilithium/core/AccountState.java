/* 
 * Copyright (C) 2018 Dilithium Team.
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
import org.dilithium.serialization.ParcelData;
import org.dilithium.serialization.Serializer;
import org.dilithium.util.ByteUtil;

/**
 * This class contains the current state of a dilithium account.
 * The state of an account is generally only changed via a transaction.
 * The class is based on EthereumJ AccountState <https://github.com/ethereum/ethereumj/blob/develop/ethereumj-core/src/main/java/org/ethereum/core/AccountState.java>.
 */
public class AccountState {
    
    /*  The amount of monolithium [MLT] owned by an address.
     *  The unit in monolithium is 1,000,000th of a dilithium. */
    private final BigInteger balance;
    
    /*  This nonce is equal to the last verified transaction nonce -
     *  the tx nonce is signed (along with other data) as part of the transaction:
     *  it prevents others replaying a transaction to continously spending it as
     *  a new valid transaction must have a value larger than this recorded nonce value. */
    private final BigInteger nonce;
    
    /*  This is only used if the account is a smart contract account:
     *  it is the hash result of the contracts bytecode. The value is used to as the index
     *  in the database where the bytecode is stored */
    private final byte[] codeHash;
    
    /*  Contains the encoded version of this object, using the internal Standard-Low-Level-Encoding system.
     *  This "serialized" object if often sent over the network to nodes (which may be running different versions of the software).
     *  (can be considered a version of the serialzed object that is cross language platform )
     *  see org.dilithium.util.SLLencoding */
    private  byte[] encoded;
    
    //  CONSTRUCTORS:
    public AccountState(){
        this(BigInteger.ZERO, BigInteger.ZERO, null);
    }
    
    public AccountState(BigInteger balance, BigInteger nonce, byte[] codeHash){
        this.balance = balance;
        this.nonce = nonce;
        this.codeHash = codeHash;
        this.encoded = null;
    }
    
    public AccountState(Integer balance, Integer nonce, byte[] codeHash){
        this(BigInteger.valueOf(balance.intValue()),BigInteger.valueOf(nonce.intValue()),codeHash);
    }
    
    public AccountState(byte[] parcel){
        this(Serializer.getParcelData(parcel), parcel);
    }
    
    public AccountState(ParcelData[] parcelData, byte[] parcel){
        this.balance = ByteUtil.bytesToBigInteger(parcelData[0].getData());
        this.nonce = ByteUtil.bytesToBigInteger(parcelData[1].getData());
        this.codeHash = parcelData[2].getData();
        this.encoded = parcel;
    }
    
    //  METHODS:
    /* balance getter */
    public BigInteger getBalance(){
        return balance;
    }
    /* nonce getter*/
    public BigInteger getNonce(){
        return nonce;
    }
    /* codeHash getter */
    public byte[] getCodeHash(){
        return codeHash;
    }
    /* sllencoded getter, if is null then it will generate the sll encoded bytes */
    public byte[] getEncoded(){
        if(encoded == null){ encoded = Serializer.createParcel(new Object[]{this.balance,this.nonce,this.codeHash}); }
        return encoded;
    }
    /* toString override */
    public String toString(){
        return "{ Balance: " + balance.toString() + ", \n" +
              "-  Nonce: " + nonce.toString() + ", \n" +
              "-  Code Hash: " + Hex.toHexString(codeHash) +
              "-  }";
    }
    /* get clone of account state with modifed balance*/
    public AccountState addBalance(BigInteger value){
        return new AccountState(this.balance.add(value), this.nonce, this.codeHash);
    }
    /* get clone of account state with modifed balance */
    public AccountState addNonce(BigInteger value){
        return new AccountState(this.balance, this.nonce.add(value), this.codeHash);
    }
    /* get clone of account state with a modifed nonce set */
    public AccountState setNonce(BigInteger value){
        return new AccountState(this.balance, value, this.codeHash);
    }
    
}
