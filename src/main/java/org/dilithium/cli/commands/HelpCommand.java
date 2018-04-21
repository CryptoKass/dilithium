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

import java.util.Map;
import org.dilithium.cli.Commander;

/**
 * This class 
 */
public class HelpCommand implements Command{

    @Override
    public String getHelp() {
         return  "cmd: -help \n" +
                "- description: Displays help for all known commands. \n" + 
                "------------------------------------------------------------------------";
    }

    @Override
    public String[] getParams() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run(String[] args) {
        Commander.CommanderPrint("\n------------------------------------------------------------------------\n"+
                "-  DILITHIUM HELP\n" +
                "------------------------------------------------------------------------");
        for( String key : Commander.getInstance().cmds.keySet() ){
            Commander.CommanderPrint(Commander.getInstance().cmds.get(key).getHelp());
        }
        
    }

}
