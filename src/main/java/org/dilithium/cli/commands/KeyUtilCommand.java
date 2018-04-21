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

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Arrays;
import org.dilithium.cli.Commander;
import org.dilithium.util.KeyUtil;

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
            PrivateKey privateKey = null;
            PublicKey publicKey = null;
            KeyPair keys = null;
            
            if(args.length > 1) {
                privateKey = KeyUtil.stringToPrivateKey(args[1]);
                publicKey = KeyUtil.privateKeyToPublicKey((ECPrivateKey)privateKey);
            }else{
                keys = KeyUtil.GenerateKeyPair();
                privateKey = keys.getPrivate();
                publicKey = keys.getPublic();
            }
            
            Commander.CommanderPrint("--- [DILITHIUM KEY PAIR] ---");
            //converting key pairs to string:
            String priv = KeyUtil.privateKeyToString((ECPrivateKey) privateKey);
            String pub = KeyUtil.publicKeyToString((ECPublicKey) publicKey);
            //converting string back to keys:
            ECPrivateKey privkey = KeyUtil.stringToPrivateKey(priv);
            ECPublicKey pubkey = KeyUtil.stringToPublicKey(pub);
            //converting key to address:
            String address = KeyUtil.publicKeyToAddressString(pubkey);
            
            Commander.CommanderPrint( "Raw-Private-Key: " + KeyUtil.privateKeyToString(privkey));
            Commander.CommanderPrint( "Raw-Public-Key:  " + KeyUtil.publicKeyToString(pubkey));
            Commander.CommanderPrint( "Address:         " + KeyUtil.publicKeyToAddressString(pubkey));
            
        }else if(args[0].equals("-help")){
            Commander.CommanderPrint(getHelp());
        }else {
            Commander.CommanderPrint("Sorry param not yet implemented");
        }
    }
    
}
