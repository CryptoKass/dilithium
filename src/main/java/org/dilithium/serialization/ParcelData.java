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

package org.dilithium.serialization;

import java.util.Arrays;

/**
 * ParcelData contains a unpacked element from a parcel and its corresponding length and prefix data.
 */
public class ParcelData {
    private byte[] data;
    private int length;
    private byte prefix;
    private static final byte PREFIXLISTBYTEARRAY   = (byte) 0b10001001;
    
    public ParcelData(byte prefix, int length, byte[] data){
        this.prefix = prefix;
        this.length = length;
        this.data = data;
    }
    
    public byte[] getData(){
        return data;
    }
    public int getLength(){
        return length;
    }
    public byte getPrefix(){
        return prefix;
    }
    
    @Override
    public String toString(){
        String s = "parcel-data:\n";
        s += " - prefix: " + prefix + "\n";
        s += " - length: " + length + "\n";
        s += " - data: " + Arrays.toString(data) + "\n";
        return s;
    }
    
}
