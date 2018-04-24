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

import java.io.IOException;
import java.util.Arrays;
import org.dilithium.Start;
import org.dilithium.cli.Commander;
import org.dilithium.core.Block;
import org.dilithium.db.Context;
import org.dilithium.serialization.Json;
import org.dilithium.util.Encoding;

/**
 * This class 
 */
public class NodeCommand implements Command {

    @Override
    public String getHelp() {
        return "cmd: node \n" +
                "- description: Command the local full-node \n" +
                "- usage: node param [situational...] \n"+
                "- param: 'start' , 'show', 'show-block' [index], 'stop' '-help', '-params' \n"+
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        return new String[]{"-help", "-params", "start", "show", "show-block", "stop", "port" };
    }

    @Override
    public void run(String[] args) {
        if( !Arrays.asList(getParams()).contains(args[0]) ){
            Commander.CommanderPrint("ERROR ! unknown parameters...");
            Commander.CommanderPrint(Arrays.toString(getParams()));
            return;
        }
        
        String[] params = getParams();
        
        if(args[0].equals(params[2])){ //start
            Commander.CommanderPrint("Starting node: ");
            Start.localNode.start();
            return;
        }
        
        if(args[0].equals(params[3])){ //show
            Commander.CommanderPrint(Start.localNode.toString());
            return;
        }
        
        if(args[0].equals(params[4])){ //show-block
            String indexString = args[1] ;
            if(indexString == null){
                Commander.CommanderPrint("ERROR ! no index included");
                return;
            }
            
            Commander.CommanderPrint("Fetching block...");
            Commander.CommanderPrint("Searching for block with index: " + indexString);          
            Context context = Start.localNode.getContext();
            long index = Long.parseLong(indexString);
            Block block = context.getBlock(index);
            
            if(block == null) {
                Commander.CommanderPrint("ERROR ! block not found.");
                return;
            }
            
            if(args[2] != null){
                if(args[2].equals("-json")){
                    Commander.CommanderPrint("found block: " + Json.createJsonPretty(block));            
                    return;
                }
                if(args[2].equals("-json-raw")){
                    Commander.CommanderPrint("found block: " + Json.createJson(block));            
                    return;
                }
                
                Commander.CommanderPrint("ERROR 2 argument unknown !");
                return;
            }
            
            Commander.CommanderPrint("found block: " + block.toString());            
            return;
        }
        
        if(args[0].equals(params[5])){ //stop
            Commander.CommanderPrint("Stopping node: ");
            try {
				Start.localNode.stop();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return;
        }
        
        if(args[0].equals(params[6])){ //port
                    
            if(args.length < 2){
                Commander.CommanderPrint("port: "+ Start.localNode.getPort());
                return;
            }
            
            String newPort = args[1];
            
            int port = Integer.parseUnsignedInt(newPort);
            if(port == 0){
                Commander.CommanderPrint("ERROR ! invalid new port.");
                return;
            }
            
            Start.localNode.setPort(port);
            Commander.CommanderPrint("New port set for local node.");
            Commander.CommanderPrint("port: " + newPort);
            return;
        }
        
        
        Commander.CommanderPrint("Sorry param not yet implemented");
        
    }
    
}
