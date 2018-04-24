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

package org.dilithium.cli;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;

import org.dilithium.cli.commands.Command;
import org.dilithium.cli.commands.HelpCommand;
import org.dilithium.cli.commands.KeyUtilCommand;
import org.dilithium.cli.commands.NodeCommand;
import org.dilithium.cli.commands.PingCommand;
import org.dilithium.cli.commands.SetDebugModeCommand;
import org.dilithium.cli.commands.WalletCommand;
import org.dilithium.util.Log;

/**
 * This class 
 */
public class Commander {
    
    public HashMap<String,Command> cmds;
    public Scanner scanner;
    public static Commander instance;
    public static boolean debugMode = false;
    
    /* we get the command object from cmds and call command.run(args)*/
    public void call(String[] rawArgs){
     
        String function = rawArgs[0];
        String[] args = Arrays.copyOfRange(rawArgs, 1,rawArgs.length);

        Command command = cmds.get(function);
        if(command == null){        
            CommanderPrint("command function: '" + function +"' not found. Type -help for a list of functions");
        }else{
            command.run(args);
        }
             
    }
    
    /* populates the command arraylist with commands. */
    public void setup(){
        cmds = new HashMap<String,Command>();
        cmds.put("key-util", new KeyUtilCommand());
        cmds.put("wallet", new WalletCommand());
        cmds.put("node", new NodeCommand());
        cmds.put("ping", new PingCommand());
        cmds.put("debug-cli", new SetDebugModeCommand());
        cmds.put("-help", new HelpCommand());
        scanner = new Scanner(System.in);
    }
    
    /* uses a scanner to get input from the user, we check if cmd was quit first then call(args). */
    public void listen(){
        while(true){
            //Display input message:
            CommanderInput("dilithium-cli");
            //Gather user input:
            String rawInput = (String) scanner.nextLine();
            
            //check if the users wishes to quit
            if(rawInput.equals("quit") || rawInput.equals("exit")){
                break;
            }
            
            //Gather the raw arguments entered by the user
            String[] rawArgs = rawInput.split("\\s+");
            
            //check any command or argument was entered
            if(!rawArgs[0].equals("")){
                /* Check if debug mode is false, if so then run command in try catch to aviod crashing the application if an error is thrown */
                if(debugMode == false){
                    try{
                        call(rawArgs);
                    }catch(ArrayIndexOutOfBoundsException e){
                        CommanderPrint("command couldn't execute, perhaps not enough arguments? try: "+ rawArgs[0] + " -help");
                    }catch(Exception e){
                        CommanderPrint("command failed to execute.");
                    }
                }else{ //Otherwise run the command with no saftey net.
                    call(rawArgs);
                }
            }
        }
    }
    
    public static Commander getInstance(){
        if(instance != null) {
            instance = new Commander();
        }
        
        return instance;
    }
    
    public Commander(){
        setup();
        instance = this;
    }
    
    /* Standard commander print method. */
    public static void CommanderPrint(String msg){
        Log.log(Level.INFO, "- " + msg);
    }
    /* Commander print method for inputs. */
    public static String CommanderInput(String msg){
        Log.log(Level.INFO, "" + msg + ": ");
        return null;
    }
    
}
