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

import java.math.BigInteger;
import java.security.interfaces.ECPrivateKey;
import java.util.Arrays;
import org.dilithium.Start;
import org.dilithium.cli.Commander;
import org.dilithium.config.NodeSettings;
import org.dilithium.core.Transaction;
import org.dilithium.core.Wallet;
import org.dilithium.util.Encoding;
import org.dilithium.util.KeyUtil;

/**
 * This class 
 */
public class WalletCommand implements Command {

    @Override
    public String getHelp() {
        return  "cmd: wallet \n" +
                "- description: Control the local wallet for this node \n" +
                "- usage: wallet param [situational...] \n"+
                "- param: 'import' (-private), show, tx (value) (recipient),'-help', '-params' \n"+
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        return new String[]{ "-help", "-params", "import", "show", "tx" };
    }

    @Override
    public void run(String[] args) {
        if( !Arrays.asList(getParams()).contains(args[0]) ){
            Commander.CommanderPrint("ERROR ! unknown parameters...");
            Commander.CommanderPrint(Arrays.toString(getParams()));
            return;
        }
        
        String[] params = getParams();
        
        if(args[0].equals(params[2])){ //import
            String privateKeyString = args[1];
            if(privateKeyString == null){
                Commander.CommanderPrint("ERROR ! no private key entered.");
                return;
            }
            try {
            ECPrivateKey privateKey = KeyUtil.stringToPrivateKey(privateKeyString);
            Start.localWallet = new Wallet(privateKey);
            Commander.CommanderPrint("Wallet Successfully imported!");
            } catch (Exception e){
                Commander.CommanderPrint("ERROR ! couldn't parse private key");
                return;
            }
        }else if(args[0].equals(params[3])){ //show
            Commander.CommanderPrint(Start.localWallet.toString(Start.localContext));
            return;
        }else if(args[0].equals(params[4])){ //transaction tx
            String value = args[1];
            String recipient = args[2];
            if (value == null || recipient == null){
                Commander.CommanderPrint("ERROR ! you must enter a value and recipient.");
                return;
            }
            
            Transaction tx = Start.localWallet.generateTransaction(new BigInteger(value), Encoding.hexToBytes(recipient),NodeSettings.getDefault().getNetworkID() , Start.localContext , NodeSettings.getDefault().getAxiom());
            Commander.CommanderPrint(tx.toString());
            Commander.CommanderPrint("Transaction Hash: " + Encoding.bytesToHex(tx.getHash()));
            Commander.CommanderPrint("Is signature valid: " + NodeSettings.getDefault().getAxiom().verifySignature(tx));
            Start.localNode.addTransactionToPool(tx);
            
            Commander.CommanderPrint("Broadcast and memepool not yet implemented...");
        }else if(args[0].equals("-help")){
            Commander.CommanderPrint(getHelp());
        }else{
            Commander.CommanderPrint("Sorry param not yet implemented");
        }
    }

}
