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
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

import org.dilithium.core.axiom.Axiom;
import org.dilithium.db.Context;
import org.dilithium.util.ByteUtil;
import org.dilithium.util.Encoding;
import org.dilithium.util.JsonUtil;
import org.dilithium.util.KeyUtil;

/**
 * This class
 */
public class Wallet {
    private ECPrivateKey privateKey;
    private ECPublicKey publicKey;
    private byte[] address;
    private Axiom axiom;

    public ECPublicKey getPublicKey() {
        return this.publicKey;
    }

    public byte[] getAddress() {
        return this.address;
    }

    @Override
    public String toString() {
        return "wallet: { \n" +
                "public-key: " + KeyUtil.publicKeyToString(publicKey) + ", \n" +
                "address: " + Encoding.bytesToAddress(address) + " \n" +
                "}";
    }

    public String toString(Context context) {
        return "wallet: { \n" +
                "public-key: " + KeyUtil.publicKeyToString(publicKey) + ", \n" +
                "address: " + Encoding.bytesToAddress(address) + ", \n" +
                "balance: " + getBalance(context) + " \n" +
                "}";
    }


    public Wallet() {
        KeyPair keys = KeyUtil.GenerateKeyPair();
        this.privateKey = (ECPrivateKey) keys.getPrivate();
        this.publicKey = (ECPublicKey) keys.getPublic();
        this.address = KeyUtil.publicKeyToAddress(publicKey);
    }

    public Wallet(ECPrivateKey privateKey) {
        this.privateKey = privateKey;
        this.publicKey = KeyUtil.privateKeyToPublicKey(privateKey);
        this.address = KeyUtil.publicKeyToAddress(publicKey);
    }

    /* returns balance as it is recorded in the given context */
    public BigInteger getBalance(Context context) {
        AccountState account = context.getAccount(address);
        if (account != null) {
            return account.getBalance();
        } else {
            return BigInteger.ZERO;
        }
    }

    /*generates a transaction, without doing any validity checks */
    public Transaction generateTransaction(BigInteger value, byte[] recipient, byte networkId, Context context, Axiom axiom) {

        byte[] nonce = ByteUtil.bigIntegerToBytes(BigInteger.ZERO);
        byte[] data = ByteUtil.intToBytes(0);
        byte[] sender = KeyUtil.encodeECPublicKey(publicKey);

        //attempt to get account nonce;
        AccountState account = context.getAccount(address);
        if (account != null) {
            nonce = ByteUtil.bigIntegerToBytes(account.getNonce());
            nonce = ByteUtil.increment(nonce);
        }

        Transaction tx = new Transaction(privateKey, nonce, ByteUtil.bigIntegerToBytes(value), data, recipient, networkId, sender, axiom);
        //Transaction(ECPrivateKey privateKey, byte[] nonce, byte[] value, byte[] data, byte[] recipient, byte networkId, byte[] sender, Axiom axiom)

        return tx;
    }

    public String getJson() {
        return JsonUtil.getJson(this);
    }
}
