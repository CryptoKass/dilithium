/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dilithium.cli.commands;

import java.util.Arrays;
import org.dilithium.cli.Commander;

/**
 * This class 
 */
public class SetDebugModeCommand implements Command {

    @Override
    public String getHelp() {
        return "cmd: debug-cli \n" +
                "- description: Toggle debug mode on the cli. Causing the application to potentially crash if an error is thrown. \n" +
                "- usage: debug-cli true/false \n"+
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        return new String[]{"-help", "-params", "true", "false"};
    }

    @Override
    public void run(String[] args) {
        
        if(args.length < 1){
            Commander.CommanderPrint("Debug mode active: " + Commander.getInstance().debugMode);
            return;
        }
        
        if( !Arrays.asList(getParams()).contains(args[0]) ){
            Commander.CommanderPrint("ERROR ! unknown parameters...");
            Commander.CommanderPrint(Arrays.toString(getParams()));
            return;
        }
        
        String[] params = getParams();
        
        if(args[0].equals(params[2])){
            Commander.getInstance().debugMode = true;
            Commander.CommanderPrint("Setting Debug mode to true...");
            Commander.CommanderPrint("Warning application may now crash if an error is thrown!");
            Commander.CommanderPrint("Debug mode active: " + Commander.getInstance().debugMode);
            return;
        }
        
        if(args[0].equals(params[3])){
            Commander.getInstance().debugMode = false;
            Commander.CommanderPrint("Setting Debug mode to false...");
            Commander.CommanderPrint("Application will not crash if an error is thrown.");
            Commander.CommanderPrint("Debug mode active: " + Commander.getInstance().debugMode);
            return;
        }
        
                
        if(args[0].equals(params[0])){ //help
            Commander.CommanderPrint(getHelp());
            return;
        }
        
        if(args[0].equals(params[1])){ //params
            Commander.CommanderPrint(Arrays.toString(getParams()));
            return;
        }
        
         Commander.CommanderPrint("Sorry param not implemented. debug-cli -help");
        
    }

}
