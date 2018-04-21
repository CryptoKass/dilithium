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

package org.dilithium.serialization;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.dilithium.util.ByteUtil;

/**
 * This class 
 */
public class Serializer {
    
    private static final byte PREFIXBYTE            = (byte)0b00001000;
    private static final byte PREFIXBYTEARRAY       = (byte)0b00001001;
    private static final byte PREFIXLISTBYTEARRAY   = (byte)0b10001001;
    private static final byte PREFIXINT             = (byte)0b00000100;
    //private static final byte PREFIXINTARRAY      = (byte)0b00000101;
    private static final byte PREFIXBIGINT          = (byte)0b00001100;
    private static final byte PREFIXSTRING          = (byte)0b00000010;
    private static final byte PREFIXSTRINGARRAY     = (byte)0b00000011;
    
    public static byte[] createParcel(Object[] contents){
        
        Object o;
        byte[] objectBytes = new byte[]{};
        byte prefix = (byte) 0;
        byte[] length = ByteUtil.shortToBytes((short) 0);
        byte[] newParcel = new byte[]{}; 
        
        for (int i=0; i < contents.length; i++){
            //System.out.print("\nO: " + i);
            o = contents[i];
            objectBytes = new byte[]{};
            
            if(o instanceof Integer){
                objectBytes = ByteUtil.intToBytes((int) o);
                prefix = PREFIXINT;
                length = ByteUtil.shortToBytes((short) 4);
            }else if(o instanceof Byte){
                objectBytes = new byte[]{(byte) o};
                prefix = PREFIXBYTE;
                length = ByteUtil.shortToBytes((short) 1);
            }else if(o instanceof Byte[] || o.getClass().isArray()){
                //System.out.println("Array found");
                objectBytes = (byte[]) o;
                prefix = PREFIXBYTEARRAY;
                length = ByteUtil.shortToBytes((short)objectBytes.length);
            }else if(o instanceof BigInteger){
                objectBytes = ByteUtil.bigIntegerToBytes((BigInteger) o);
                prefix = PREFIXBIGINT;
                length = ByteUtil.shortToBytes((short)objectBytes.length);
            }else if(o instanceof String){
                objectBytes = ByteUtil.stringToBytes((String) o);
                prefix = PREFIXSTRING;
                length = ByteUtil.shortToBytes((short)objectBytes.length);
            }else if(o instanceof String[]){
                prefix = PREFIXSTRINGARRAY;
                String[] strArray = (String[]) o;
                for(int j=0; j<strArray.length; j++){
                    byte[] str = ByteUtil.stringToBytes( strArray[j] );
                    byte strlength = (byte) str.length;
                    objectBytes = ByteUtil.concatenateBytes(objectBytes, new byte[] {strlength});
                    objectBytes = ByteUtil.concatenateBytes(objectBytes, str);
                }
                length = ByteUtil.shortToBytes((short)objectBytes.length);               
            }else if(o instanceof List<?>){
                prefix = PREFIXLISTBYTEARRAY;
                List<byte[]> byteArr = (ArrayList<byte[]>) o;
                for(int j=0; j< byteArr.size(); j++){
                    byte[] bytes = byteArr.get(j);
                    byte[] bytesLength = ByteUtil.shortToBytes((short) bytes.length);
                    objectBytes = ByteUtil.concatenateBytes(objectBytes, bytesLength);
                    objectBytes = ByteUtil.concatenateBytes(objectBytes, bytes);
                }
                length = ByteUtil.shortToBytes((short)objectBytes.length);
            }
            newParcel = addToParcel(newParcel, prefix, length, objectBytes);
            //System.out.print(", L: " + objectBytes.length);
        }
        return newParcel;
    }
    
    public static byte[] addToParcel(byte[] parcel ,byte prefix, byte[] length, byte[] data){
        parcel = ByteUtil.concatenateBytes(parcel, new byte[] {prefix});
        parcel = ByteUtil.concatenateBytes(parcel, length);
        parcel = ByteUtil.concatenateBytes(parcel, data);
        return parcel;
    }
    
    public static Object[] getFromParcel(byte prefix, byte length, byte[] data){
        
        ArrayList<Object> contents = new  ArrayList<Object>();
        Object o = null;
        
        switch(prefix){
            case PREFIXINT:
                o = (Object) ByteUtil.bytesToInt(data);
                break;
            case PREFIXSTRING:
                o = (Object) ByteUtil.bytesToString(data);
                break;
            case PREFIXSTRINGARRAY:
                o = (Object) ByteUtil.bytesToStringArray(data);
                break;
            case PREFIXBYTE:
                o = (Object) data[0];
                break;
            case PREFIXBYTEARRAY:
                o = (Object) data;
                break;
            case PREFIXBIGINT:
                o = (Object) ByteUtil.bytesToBigInteger(data);
                break;
        }
        contents.add(o);
        
        return contents.toArray();
    }
    
    public static ParcelData[] getParcelData(byte[] parcel){
        ArrayList<ParcelData> contents = new ArrayList<ParcelData>();
        int cursor = 0;
        byte prefix;
        byte[] data;
        int length;
        
        while(cursor < parcel.length-1){
            //get prefix
            prefix = parcel[cursor];
            cursor++; //move cursor along to length byte
            length = (int) ByteUtil.getShortFromBytes(Arrays.copyOfRange(parcel,cursor,cursor+2));
            cursor+=2; //move cursor along to start of data bytes
            data = Arrays.copyOfRange(parcel,cursor,cursor+length);
            cursor += length; //move cursor to end of content 
            contents.add(new ParcelData(prefix,length,data));
        }
        
        return contents.toArray(new ParcelData[contents.size()]);
    }
    
}
