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

package org.dilithium.cli.commands;

import java.util.Arrays;

import org.bouncycastle.util.encoders.Hex;
import org.dilithium.cli.Commander;
import org.dilithium.util.ecdsa.ECKey;

/**
 * This class 
 */
public class KeyUtilCommand implements Command {

    @Override
    public String getHelp() {
        return  "cmd: key-util \n" +
                "- description: A tool for creating and/or extracting keys \n" +
                "- usage: key-util param [situational...] \n"+
                "- param: 'generate' [-private], 'pem' location [-private], 'info', '-help', '-params' \n"+
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        return new String[]{ "-help", "-params", "generate", "pem", "info" };
    }

    @Override
    public void run(String[] args) {
        if( !Arrays.asList(getParams()).contains(args[0]) ){
            Commander.CommanderPrint("ERROR ! unknown parameters...");
            Commander.CommanderPrint(Arrays.toString(getParams()));
            return;
        }
        
        if(args[0].equals("generate")){
            ECKey keyPair = null;

            
            if(args.length > 1) {
                keyPair = ECKey.fromPrivate(Hex.decode(args[1]));
            }else{
                keyPair = new ECKey();
            }
            
            Commander.CommanderPrint("--- [DILITHIUM KEY PAIR] ---");
            //converting key pairs to string:
            String priv = Hex.toHexString(keyPair.getPrivKeyBytes());
            String pub = Hex.toHexString(keyPair.getPubKey());
            //converting string back to keys:
            keyPair = ECKey.fromPrivate(Hex.decode(priv));
            //converting key to address:
            String address = Hex.toHexString(keyPair.getAddress());
            
            Commander.CommanderPrint( "Raw-Private-Key: " + priv);
            Commander.CommanderPrint( "Raw-Public-Key:  " + pub);
            Commander.CommanderPrint( "Address:         " + address);
            
        }else if(args[0].equals("-help")){
            Commander.CommanderPrint(getHelp());
        }else {
            Commander.CommanderPrint("Sorry, param not yet implemented");
        }
    }
    
}
