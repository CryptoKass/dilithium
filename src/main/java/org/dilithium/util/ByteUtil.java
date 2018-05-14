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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.security.spec.ECPoint;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;

/**
 * This class handles conversion to and from byte/byte[] as well as some other useful byte methods,
 * some of the conversions may be already part of java standard, however at some point in time we may
 * wish to change how this is done, so its useful to only have to change it in one place.
 */
public class ByteUtil {
    
    public static int getUnsignedByte(byte input) {
        return input & 0xFF;
  }
    
    public static byte[] charArrayToBytes(char[] input){

        CharBuffer charBuffer = CharBuffer.wrap(input);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return bytes;

    }
    
    /* short hand method that converts string to bytes, ensures charset is UTF-8 */
    public static byte[] stringToBytes(String input){
        return input.getBytes(Charset.forName("UTF-8"));
    }
    
    /* converts long to bytes, useful for hashing or serialization */
    public static byte[] longToBytes(long input){
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(input);
        return buffer.array();
    }
    
    /* converts a BigInteger to bytes, useful for hashing or serialization */
    public static byte[] bigIntegerToBytes(BigInteger value) {
        if (value == null)
            return null;

        byte[] data = value.toByteArray();

        if (data.length != 1 && data[0] == 0) {
            byte[] tmp = new byte[data.length - 1];
            System.arraycopy(data, 1, tmp, 0, tmp.length);
            data = tmp;
        }
        return data;
    }

    public static byte[] bigIntegerToBytes(BigInteger b, int numBytes) {
        if (b == null)
            return null;
        byte[] bytes = new byte[numBytes];
        byte[] biBytes = b.toByteArray();
        int start = (biBytes.length == numBytes + 1) ? 1 : 0;
        int length = Math.min(biBytes.length, numBytes);
        System.arraycopy(biBytes, start, bytes, numBytes - length, length);
        return bytes;
    }
    
    /* int to bytes */
    public static byte[] intToBytes(int input){
        return ByteBuffer.allocate(4).putInt(input).array();
    }
    
    /* short to bytes */
    public static byte[] shortToBytes(short input){
        return ByteBuffer.allocate(2).putShort(input).array();
    }
    
    public static short getShortFromBytes(byte[] input){
        return ByteBuffer.wrap(input).getShort();
    }
   
    /* converts bytes back into an Integer */
    public static int bytesToInt(byte[] input){
        return ByteBuffer.wrap(input).getInt();
    }
    
    /* converts bytes back into a string, attempts to use UTF-8 first */
    public static String bytesToString(byte[] input){     
        try {
            //try to create new bytes uing utf-8
            return new String(input, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            //strange utf-8 not supported, try another default (which should be utf-8)
            return new String(input);
        }
    }
    
    /* converts bytes back into Big Integer */
    public static BigInteger bytesToBigInteger(byte[] input){
        return new BigInteger(input);
    }
    
    /* converts bytes back into a string array */
    public static String[] bytesToStringArray(byte[] input){
        //TODO: populate method:
        return null;
    }
    
    /* concats two byte arrays [] into one in the order "ab". */
    public static byte[] concatenateBytes(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length]; 
        System.arraycopy(a, 0, result, 0, a.length); 
        System.arraycopy(b, 0, result, a.length, b.length); 
        return result;
    }
    
    /* Converts ecPoint to a bytearray, java.security.spec.ECPoint not to be confused with bouncey castle ecpoint.*/
    public static byte[] ecPointToBytes( ECPoint input ){
        BigInteger qx = input.getAffineX();
        BigInteger qy = input.getAffineY();
        byte[] qyBytes = bigIntegerToBytes(qy);
        byte[] qxBytes = bigIntegerToBytes(qx);
        byte[] xlengthBytes = new byte[1]; //info bit
        
        
        //fix length issues
        xlengthBytes[0] = (byte) qxBytes.length;

        byte[] buffer = concatenateBytes(xlengthBytes,bigIntegerToBytes(qx));
        buffer = concatenateBytes(buffer,bigIntegerToBytes(qy));
        return buffer;        
    }
    
    /* Converts byte array bck into an ecpoint */
    public static ECPoint bytesToECPoint(byte[] input ){
        int midpoint = (int) input[0];
        BigInteger qx = bytesToBigInteger(Arrays.copyOfRange(input,1, midpoint+1));
        BigInteger qy = bytesToBigInteger(Arrays.copyOfRange(input,midpoint+1,input.length));
        return new ECPoint(qx,qy);
    }
    
    //DER
    public static ASN1Primitive toAsn1Object(byte[] data) throws IOException
    {
        ByteArrayInputStream inStream = new ByteArrayInputStream(data);
        ASN1InputStream asnInputStream = new ASN1InputStream(inStream);

        return asnInputStream.readObject();
    }
    
    //Increment 
    public static byte[] increment(byte[] input) {
       /* boolean carryOver = true;
        int len = (input.length - 1);
        for (int i = len; i >= 0; i--) {
            if (carryOver) {
                if (input[i] == 0) {
                    input[i] = 1;
                    carryOver = false;
                }
                else {
                    input[i] = 0;
                    carryOver = true;
                }
            }
        }*/
        // i = bytesToInt(input);
        //i++;
        
        //return intToBytes(i);
        
        BigInteger i = bytesToBigInteger(input);
        i = i.add(BigInteger.ONE);
        
        return bigIntegerToBytes(i);
        
    }
    
    //Populate with
    public static byte[] populate(byte[] array,byte input){
        for(int i=0;i < array.length; i++){
            array[i] = input;
        }
        return array;
    }
    
    //get byte time
    public static byte[] getNowTimeStamp(){
        int dateInSec = (int) (System.currentTimeMillis() / 1000);
        return ByteBuffer.allocate(4).putInt(dateInSec).array();
        
    }
    
    public static List<byte[]> parcelDataToListBytes( byte[] parcelData ){
        int cursor  = 0;
        int length = 0;
        List<byte[]> listBytes = new ArrayList<byte[]>();
        while(cursor < parcelData.length-1){
            length = (int) ByteUtil.getShortFromBytes(Arrays.copyOfRange(parcelData,cursor,cursor+2));
            cursor+=2; //move cursor along to start of data bytes
            listBytes.add(Arrays.copyOfRange(parcelData,cursor,cursor+length));
            cursor += length; //move cursor to end of content           
        }
        return listBytes;
    }

    public static byte[] merge(byte[]... arrays)
    {
        int arrCount = 0;
        int count = 0;
        for (byte[] array: arrays)
        {
            arrCount++;
            count += array.length;
        }

        // Create new array and copy all array contents
        byte[] mergedArray = new byte[count];
        int start = 0;
        for (byte[] array: arrays) {
            System.arraycopy(array, 0, mergedArray, start, array.length);
            start += array.length;
        }
        return mergedArray;
    }


    
   
} 

