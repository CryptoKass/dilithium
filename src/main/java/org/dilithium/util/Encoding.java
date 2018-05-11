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

import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

/**
 * This class contains methods for encoding and decoding. 
 */
public class Encoding {
    
    /*  Returns bytes as a hexadecimal string,
     *  this is the standard encoding method */
    public static String bytesToHex(byte[] input){
        StringBuffer hexString = new StringBuffer();
        
        for (int i = 0; i < input.length; i++) {
            String hexSegment = Integer.toHexString(0xff & input[i]);
            if(hexSegment.length() == 1) hexString.append('0');
            hexString.append(hexSegment);
        } 
    return hexString.toString();
    }

    /* For encoding a single byte as a hexadecimal string */
    public static String byteToHex(byte input) {
        byte[] temp = {input};
        return Hex.toHexString(temp);
    }
    
    /*  Hex encodes bytes and returns a string that follows address formatting rules.
     *  Address must have a prefix, either Cx for a contract or 0x for a personal account,
     *  this formatting allows users to quickly distinguish between account types,
     *  though it has no impact on the functionality of the accounts. */
    public static String bytesToAddress(byte[] input, boolean isContract){
        String compressedPublicKey = bytesToHex(bytesToCompressedAddress(input));
        //String prefix = (isContract) ? "Cx" : "0x";
        return /*prefix +*/ compressedPublicKey;
    }
    
    public static byte[] bytesToCompressedAddress(byte[] input){
        return Arrays.copyOfRange(input, input.length-20, input.length);
    }
    
    /*  Hex encodes bytes and returns a string that follows address formatting rules.
     *  This is method creates an address with the personal account prefix */
    public static String bytesToAddress(byte[] input){
        return bytesToAddress(input, false);
    }
    
    /*  Decodes a hex string back into bytes */
    public static byte[] hexToBytes(String input){
        int len = input.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(input.charAt(i), 16) << 4)
                                 + Character.digit(input.charAt(i+1), 16));
        }
        return bytes;
    }
   
    /*  Returns addresses bytes, without the prefix */
    public static byte[] addressToBytes(String input){
        String importantSubString = input.substring(2,input.length());
        return hexToBytes(importantSubString);
    }
   
}
