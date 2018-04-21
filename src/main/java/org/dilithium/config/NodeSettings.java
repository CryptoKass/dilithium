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

package org.dilithium.config;

import org.dilithium.core.axiom.Axiom;
import org.dilithium.core.axiom.AxiomManager;

/**
 * This class contains all the node settings 
 */
public class NodeSettings {
    
    /*  The name of the node is used as light identification on the network.
     *  Can be used by clients to ditiguish one node from another */
    private final String nodeName;
    
    /*  The port that new peers can connect through, by default this is port 1388 */
    private final int peerPort;
    
    /*  The port that new rpc clients can connect through, by default this is port 1389 */
    private final int rpcPort;
    
    /*  The name of the network this node is interfacing with,
        This helps nodes distinguish between different networks in the event of forking */
    private final byte networkID;
    
    /* The current axiom in use by the primary network */
    private final Axiom axiom;
    
    // CONSTRUCTORS
    public NodeSettings(String nodeName, int peerPort, int rpcPort, byte networkID, Axiom axiom){
        this.nodeName = nodeName;
        this.peerPort = peerPort;
        this.rpcPort = rpcPort;
        this.networkID = networkID;
        this.axiom = axiom;
    }
    
    public static NodeSettings getDefault(){
        return new NodeSettings("just-another-dilithium-node", 1388, 1389, (byte) 0, AxiomManager.getAxiom("axiomD0") );
    }
    
    // METHODS
    /* nodeName getter */
    public String getName(){
        return nodeName;
    }
    /* peerPort getter */
    public int getPeerPort(){
        return peerPort;
    }
    /* rpcPort getter */
    public int getRpcPort(){
        return rpcPort;
    }
    /* networkUsed getter */
    public byte getNetworkID(){
        return networkID;
    }
    
    public Axiom getAxiom(){
        return axiom;
    }
    
    /* toString override */
    public String toString(){
        return "{ name: " + nodeName + ", \n" +
               "  peerPort: " + Integer.toString(peerPort) + ", \n" +
               "  rpcPort: "  + Integer.toString(rpcPort) + ", \n" +
               "  networkUsed: " + ((int) networkID) +
               " }";
    }
    
}
