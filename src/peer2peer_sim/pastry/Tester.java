/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer2peer_sim.pastry;

import java.io.IOException;
import static java.lang.Math.abs;
import java.net.UnknownHostException;

/**
 *
 * @author nawfal
 */
public class Tester {
    
    
    public static void main(String [] args) throws UnknownHostException, IOException, ClassNotFoundException
   {
       
        Thread bootstraper = new Thread( new Pastry_node("bootstraper","8081"));
        bootstraper.start();
        
       for(int i=1;i<5;i++){
           System.out.println("creating node:"+i);
           Thread new_node = new Thread( new Pastry_node("host"+i,Integer.toString(8081+i)));
           new_node.start();
       }
   }
}
