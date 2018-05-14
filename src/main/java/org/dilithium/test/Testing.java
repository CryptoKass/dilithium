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

package org.dilithium.test;

import java.io.File;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Arrays;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.encoders.Hex;
import org.dilithium.core.AccountState;
import org.dilithium.core.Transaction;
import org.dilithium.core.axiom.Axiom;
import org.dilithium.core.axiom.AxiomManager;
import org.dilithium.db.Storage;
import org.dilithium.serialization.ParcelData;
import org.dilithium.serialization.Serializer;
import org.dilithium.util.ByteUtil;
import org.dilithium.util.Encoding;
import org.dilithium.util.HashUtil;



/**
 * This class 
 */
public class Testing {
    
    public static int totalTestsRun = 0;
    public static Axiom testAxiom;
    
    public Axiom getTestAxiom(){
        if(testAxiom == null){
            testAxiom = AxiomManager.getAxiom( new byte[] { (byte) 0, (byte) 0 } );
        }
        return testAxiom;
    }
    
    
    
}
