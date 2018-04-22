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

package org.dilithium;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dilithium.cli.Commander;
import org.dilithium.config.NodeSettings;
import org.dilithium.core.Node;
import org.dilithium.core.Wallet;
import org.dilithium.db.Context;
import org.dilithium.util.KeyUtil;

/**
 * This is the class that is run when the program is started.
 */
public class Start {
    
    public static NodeSettings config;
    public static Wallet localWallet;
    public static Context localContext;
    public static Node localNode;
    
    /* This method is run when the program first starts */
    public static void main(String[] args) {
        System.out.println("-----------------------------------------");
        System.out.println("Dilithium core is starting up: ");
        System.out.println("-----------------------------------------");
        
        /* Set default settings */
        config = NodeSettings.getDefault();
        
        /* Setup bouncey castle as security provider */
        Security.addProvider(new BouncyCastleProvider());
        
        /* Setup EC References */
        KeyUtil.SetupEC();
        
        /* Get local database and storage context 
         * will contain the local state of blocks and accounts*/
        localContext = new Context(); 
        System.out.println("\nLocalContext: " + localContext.toString());
        
        /* Setup temp local wallet */
        localWallet = new Wallet();
        System.out.println("\nLocalWallet: " + localWallet.toString(localContext));
       
        /* Setup local Node */
        localNode = new Node();
        System.out.println("\nLocalNode: " + localNode.toString());
        localNode.start();
        
        /*Test */
        //Setup cli
        System.out.println("\n-----------------------------------------");
        System.out.println("Dilithium cli is starting up: ");
        System.out.println("-----------------------------------------");
        
        Commander commander = new Commander();
        commander.listen();
   
        
    }
    
}
