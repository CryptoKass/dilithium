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

package org.dilithium.util;

import java.security.MessageDigest;
import java.util.logging.Level;

/**
 * This class contains helpers for the hashing algorithms
 */
public class HashUtil {
    
    /* Shorthand: applies SHA256 algorithm to an input and returns the "hashed" bytes */
    public static byte[] applySha256(byte[] input){
        return hashWith(input,"SHA-256");
    }
    
    /* Shorthand: applies Blake2s algorithm to an input and returns the "hashed" bytes, default used */
    public static byte[] applyBlake2b(byte[] input){
        return hashWith(input,"BLAKE2B-256");
    }

    /* Shorthand: applies Sha3 algorithm to an input and returns the "hashed" bytes */
    public static byte[] applySha3(byte[] input){
        return hashWith(input,"SHA3-256");
    }
    
    /* Shorthand: applies Sha3 algorithm to an input and returns the "hashed" bytes */
    public static byte[] applyKeccak(byte[] input){
        return hashWith(input,"KECCAK-256");
    }
    
    /* apply a selected hash instance to a string */
    public static byte[] hashWith(byte[] input, String instanceName){
        try{
            MessageDigest digest = MessageDigest.getInstance(instanceName, "BC");
            byte[] hash = digest.digest(input);
            return hash;
        }catch(Exception e){
            Log.log(Level.WARNING, e.getMessage());
            return null;
        }
    }
}
