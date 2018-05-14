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

package org.dilithium.db;

import java.math.BigInteger;
import java.util.HashMap;

import org.dilithium.core.AccountState;
import org.dilithium.core.Block;
import org.dilithium.core.BlockHeader;
import org.dilithium.core.Transaction;
import org.dilithium.util.ByteArrayKey;
import org.dilithium.util.ByteUtil;
import org.dilithium.util.JsonUtil;

/**
 * This class
 */
public class StorageContext {
    // Parent to route to :
    public StorageContext parent;
    public boolean defaultToMain;

    public HashMap<ByteArrayKey, AccountState> accounts;
    public HashMap<ByteArrayKey, Transaction> mempool;
    public HashMap<ByteArrayKey, BlockHeader> headers;
    public HashMap<ByteArrayKey, Block> blocks;

    //Account state methods.
    public AccountState getAccount(byte[] address) {
        return this.getAccount(new ByteArrayKey(address));
    }

    public AccountState getAccount(ByteArrayKey addressKey) {
        AccountState account = null;

        //attempt to find account in current context...
        account = accounts.get(addressKey);
        if (account != null) return account;

        //attempt to find account in parent context...
        if (parent != null) {
            account = parent.getAccount(addressKey);
            if (account != null) return account;
        }

        //otherwise default to main database...
        if (defaultToMain) {
            byte[] accountParcel = Storage.getInstance().get(StorageMaps.ACCOUNTS, addressKey.toByteArray());
            if (accountParcel != null) {
                return new AccountState(accountParcel);
            }
        }

        //otherwise account doesnt exist in the current context.
        //create a fresh account state
        return new AccountState();
    }

    public void putAccount(ByteArrayKey addressKey, AccountState account) {
        accounts.put(addressKey, account);
    }

    public void saveAccountsToDB() {
        for (ByteArrayKey key : accounts.keySet()) {
            Storage.getInstance().put(StorageMaps.ACCOUNTS, key.toByteArray(), accounts.get(key).getEncoded());
        }

    }

    //Block Methods:
    public Block getBlock(ByteArrayKey key) {
        Block block = null;

        //attempt to find account in current context...
        block = blocks.get(key);
        if (block != null) return block;

        //attempt to find account in parent context...
        if (parent != null) {
            block = parent.getBlock(key);
            if (block != null) return block;
        }

        //otherwise default to main...
        if (defaultToMain) {
            byte[] blockParcel = Storage.getInstance().get(StorageMaps.BLOCKS, key.toByteArray());
            if (blockParcel != null) {
                //rebuild Block from retrieved blockparcel:
                return new Block(blockParcel);
            }
        }

        //otherwise block doesnt exist in the current context.
        return null;
    }

    public Block getBlock(long index) {
        byte[] key = ByteUtil.bigIntegerToBytes(BigInteger.valueOf(index));
        return this.getBlock(new ByteArrayKey(key));
    }

    public void putBlock(Block block) {
        byte[] indexBytes = ByteUtil.bigIntegerToBytes(BigInteger.valueOf(block.header.getIndex()));
        blocks.put(new ByteArrayKey(indexBytes), block);
    }

    public void saveBlocksToDB() {
        for (ByteArrayKey key : blocks.keySet()) {
            Storage.getInstance().put(StorageMaps.BLOCKS, key.toByteArray(), blocks.get(key).getEncoded());
        }
    }

    public long calculateChainSize() {
        long largestIndex = 0L;
        long current;
        for (ByteArrayKey indexKey : blocks.keySet()) {
            current = ByteUtil.bytesToBigInteger(indexKey.toByteArray()).longValue();
            largestIndex = (current > largestIndex) ? current : largestIndex;
        }
        return largestIndex + 1;
    }

    //Constructors
    public StorageContext() {
        this(null, false);
    }

    public StorageContext(StorageContext parent, boolean defaultToMain) {
        this.parent = parent;
        this.defaultToMain = defaultToMain;
        this.accounts = new HashMap<ByteArrayKey, AccountState>();
        this.mempool = new HashMap<ByteArrayKey, Transaction>();
        this.headers = new HashMap<ByteArrayKey, BlockHeader>();
        this.blocks = new HashMap<ByteArrayKey, Block>();
    }

    public String getJson() {
        return JsonUtil.getJson(this);
    }

    // -
    @Override
    public String toString() {
        return "context-content: { \n" +
                "accounts: " + accounts.size() + ", \n" +
                "blocks: " + blocks.size() + ", \n" +
                "mempool: " + mempool.size() + " },\n" +
                "}";
    }

}
