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

import org.dilithium.config.DirectoryManagement;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.DBMaker.Maker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

/**
 * This class Interfaces with the MapDB database which is used for 
 * both special memory (off-heap) storage and hard disk storage (.db files)
 */
public class Storage {
    
    /* the main instance: */
    private static Storage instance;
    
    /* path names for the database files */
    private String pathDB = DirectoryManagement.getInternalPath("db");
    private String pathAccountsDB = pathDB + "/accounts.db";
    private String pathBlocksDB = pathDB + "/blocks.db"; 
    private String pathTransactionsDB = pathDB + "/tx.db";
    private String pathContractCodeDB = pathDB + "/contracts.db";    
    private String pathContractStateDB = pathDB + "/contractstate.db";
    
    /* db objects */
    private DB accountsDB;
    private DB blocksDB;
    private DB transactionsDB;
    private DB contractCodeDB;
    private DB contractStatesDB;
    
    /* db key value stores */
    private HTreeMap<byte[], byte[]> accountsMap;
    private HTreeMap<byte[], byte[]> blocksMap;
    private HTreeMap<byte[], byte[]> transactionsMap;
    private HTreeMap<byte[], byte[]> contractCodeMap;
    private HTreeMap<byte[], byte[]> contractStatesMap;
    
    /* this exists to prevent instantiation from outside of the getInstance method */
    protected Storage(){
        /* create or open-connection the databases optionally with with crash corruption resistance "transactionEnable" and Mmap if on 64 bit system: */
        accountsDB          = getDB(pathAccountsDB,false,true);
        blocksDB            = getDB(pathBlocksDB,false,true);
        transactionsDB      = getDB(pathTransactionsDB,false,true);
        contractCodeDB      = getDB(pathContractCodeDB,false,true);
        contractStatesDB    = getDB(pathContractStateDB,false,true);
        
        /* create or open the the keyvalue stores: */
        accountsMap         = accountsDB.hashMap("map").keySerializer(Serializer.BYTE_ARRAY).valueSerializer(Serializer.BYTE_ARRAY).createOrOpen();
        blocksMap           = blocksDB.hashMap("map").keySerializer(Serializer.BYTE_ARRAY).valueSerializer(Serializer.BYTE_ARRAY).createOrOpen();
        transactionsMap     = transactionsDB.hashMap("map").keySerializer(Serializer.BYTE_ARRAY).valueSerializer(Serializer.BYTE_ARRAY).createOrOpen();
        contractCodeMap     = contractCodeDB.hashMap("map").keySerializer(Serializer.BYTE_ARRAY).valueSerializer(Serializer.BYTE_ARRAY).createOrOpen();
        contractStatesMap   = contractStatesDB.hashMap("map").keySerializer(Serializer.BYTE_ARRAY).valueSerializer(Serializer.BYTE_ARRAY).createOrOpen();
        
        //testing
    }
    
    /* gets the main instance of this object */
    public static Storage getInstance(){
        if(instance == null){
            instance = new Storage();
        }
        return instance;
    }
    
    /*  put is used to update or create a entry into the database map,
     *  the change to the db wont actually be saved until you run commit() on the
     *  returned db object. */
    public DB put(StorageMaps map, byte[] key, byte[] value){
        if(map == StorageMaps.ACCOUNTS){
            accountsMap.put(key, value);
            return accountsDB;
        }else if(map == StorageMaps.BLOCKS){
            blocksMap.put(key,value);
            return blocksDB;
        }else if(map == StorageMaps.TRANSACTIONS){
            transactionsMap.put(key,value);
            return transactionsDB;
        }else if(map == StorageMaps.CONTRACT_CODE){
            contractCodeMap.put(key,value);
            return contractCodeDB;
        }else if(map == StorageMaps.CONTRACT_STATE){
            contractStatesMap.put(key,value);
            return contractStatesDB;
        }
        else return null;
    }
    
    /*  get is used to gather the value associated with a given key in the
     *  database map.    */
    public byte[] get(StorageMaps map, byte[] key){
        if(map == StorageMaps.ACCOUNTS){
            return accountsMap.get(key);
        }else if(map == StorageMaps.BLOCKS){
            return blocksMap.get(key);
        }else if(map == StorageMaps.TRANSACTIONS){
            return transactionsMap.get(key);
        }else if(map == StorageMaps.CONTRACT_CODE){
            return contractCodeMap.get(key);
        }else if(map == StorageMaps.CONTRACT_STATE){
            return contractStatesMap.get(key);
        }
        return null;
    }
    
    /* commits changes to all database files */
    public void commitAll(){
        accountsDB.commit();
        blocksDB.commit();
        transactionsDB.commit();
        contractCodeDB.commit();
        contractStatesDB.commit();
    }
    
    /*  Safest way to closes all connections to the database,
     *  however the database will attempt to close itself when JVM finishes (quits) */
    public void closeAll(){
        accountsDB.close();
        blocksDB.close();
        transactionsDB.close();
        contractCodeDB.close();
        contractStatesDB.close();
    }
    
    /* Returns a DB connection, it will create a new db if one doesnt exist at the path */
    private DB getDB(String path, boolean safeMode, boolean autoCleanup){
        Maker dbConnection = DBMaker.fileDB(path).fileMmapEnable();
        if(safeMode){
            dbConnection = dbConnection.transactionEnable();
        }
        if (autoCleanup){
            dbConnection.closeOnJvmShutdown();
        }
        return dbConnection.make();        
    }
}
