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

import java.util.Arrays;

public final class ByteArrayKey
{
    private final byte[] data;

    public ByteArrayKey(byte[] data)
    {
        if (data == null)
        {
            throw new NullPointerException();
        }
        this.data = data;
    }
    
    public ByteArrayKey(byte data) {
    		this.data = new byte[0];
    		this.data[0] = data;
    }
    
    public ByteArrayKey(byte[] data, int a, int b) {
    		this.data = new byte[b-a+1];
    		for(int i = 0; i < b-a+1; i++) {
    			this.data[i] = data[i+a];
    		}
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof ByteArrayKey))
        {
            return false;
        }
        return Arrays.equals(data, ((ByteArrayKey)other).data);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(data);
    }
    
    public byte[] toByteArray(){
        return data;
    }
}

