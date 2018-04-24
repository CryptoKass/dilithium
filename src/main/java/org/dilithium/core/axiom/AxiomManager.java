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

package org.dilithium.core.axiom;

import java.util.HashMap;
import java.util.logging.Level;

import org.dilithium.util.ByteArrayKey;
import org.dilithium.util.Log;

/**
 * This class 
 */
public class AxiomManager {
    
    private static HashMap<ByteArrayKey,Axiom> axioms = new HashMap<ByteArrayKey,Axiom>();
    private static HashMap<String,ByteArrayKey> axiomKeys = new HashMap<String,ByteArrayKey>();
    private static boolean isPopulated = false;
    
    public static Axiom getAxiom( byte[] axiomID ){
        if(!isPopulated){createAxioms();}
        //System.out.println("gathered axiom:" + axioms.get(new ByteArrayKey(axiomID)));
        return axioms.get(new ByteArrayKey(axiomID));
    }
    
    public static Axiom getAxiom (String key){
        if(!isPopulated){createAxioms();}
        return axioms.get(axiomKeys.get(key));
    }
    
    public static void createAxioms(){
        axioms = new HashMap<ByteArrayKey,Axiom>();
        //byte array keys for each registered axiom:
        ByteArrayKey axiomD0 = new ByteArrayKey(new byte[] { (byte) 0, (byte) 0 });
        
        //put the axioms in the list and add the byteArrayKey to the shorthand axiomkeys
        axioms.put( axiomD0 , new AxiomD0() );
        axiomKeys.put( "axiomD0", axiomD0);
        
        isPopulated = true;
        Log.log(Level.INFO, "Axioms populated");
    }
    
}
