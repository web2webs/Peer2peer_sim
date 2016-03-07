/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer2peer_sim.pastry;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 *
 * @author nawfal
 */
public class NewClass {
    
    
    
    public static void main(String [] args) throws UnknownHostException, IOException, ClassNotFoundException
   {
       
        Thread bootstraper = new Thread( new Pastry_node("bootstraper","8089"));
        bootstraper.start();
        
       
   }
}
