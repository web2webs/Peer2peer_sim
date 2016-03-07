/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer2peer_sim.pastry;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import static java.lang.Math.abs;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.Spliterators.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SocketHandler;

/**
 *
 * @author nawfal
 */
public class Pastry_node implements Runnable {
    String host_name,host_IP;
    boolean state;
    int host_ID,max_Leafset,min_Leafset,L_2;
    long last_time_update;
    HashMap<Integer,String> Leaf_set_H= new HashMap<>();
    HashMap<Integer,String> Leaf_set_L= new HashMap<>();
    HashMap<Integer,String> Routing_table= new HashMap<>();
    HashMap<Integer,String> Neighborhood_set= new HashMap<>();
    HashMap<Integer,String> Local_files= new HashMap<>();
    HashMap<Integer,String> Others_files= new HashMap<>();
    public Socket port_number_to_socket(String port_number) throws IOException{
       Socket s = new Socket("127.0.0.1",Integer.parseInt(port_number));
       return s;
    } 
    public void initialize_state_request(String request_source,Socket s) throws IOException{
        
        try (ObjectOutputStream os = new ObjectOutputStream( new BufferedOutputStream(s.getOutputStream()))) {
                os.writeObject("msg_type==request_state_table");
                os.writeObject("node_IP");
                os.writeObject(this.host_IP);
                
        }
    }
    public void answer_initialize_state_request(String request_source) throws IOException{
        send_hashmap_socket("Neighborhood_set",request_source,this.Neighborhood_set);
        send_hashmap_socket("routing_table",request_source,this.Routing_table);
        HashMap leaf_set_map = new HashMap();
        leaf_set_map.putAll(this.Leaf_set_H);
        leaf_set_map.putAll(this.Leaf_set_L);
        send_hashmap_socket("leaf_set",request_source,leaf_set_map);
    }
    public String send_hashmap_socket(String hash_map_type,String port_number,HashMap<Integer,String> hashmap) throws IOException{
       System.out.println("trying too sen hashmap from:"+this.host_IP+ " to " +port_number);
       Socket s= port_number_to_socket(port_number);
       String remote_state;
       //Socket s = new Socket("127.0.0.1",port_number);
     /*  try (PrintStream p = new PrintStream(s.getOutputStream())) {
                
                p.println("msg_type==answer_hashmap");
                if("leaf_set".equals(hash_map_type)){
                    p.println("hash_map_type==leaf_set");
                   // p.flush();
                }else if("routing_table".equals(hash_map_type)){
                    p.println("hash_map_type==routing_table");
                    //p.flush();
                }else if ("Neighborhood_set".equals(hash_map_type)){
                    p.println("hash_map_type==Neighborhood_set");
                   // p.flush();
                }else{
                    p.println("hash_map_type==none");
                    //p.flush();
                }
                p.flush();
        }*/
       
       System.out.println(hashmap.size());
       try(ObjectOutputStream os = new ObjectOutputStream( new BufferedOutputStream(s.getOutputStream()))){
            os.writeObject("msg_type==answer_hashmap");
                if("leaf_set".equals(hash_map_type)){
                    os.writeObject("hash_map_type==leaf_set");
                   // p.flush();
                }else if("routing_table".equals(hash_map_type)){
                    os.writeObject("hash_map_type==routing_table");
                    //p.flush();
                }else if ("Neighborhood_set".equals(hash_map_type)){
                    os.writeObject("hash_map_type==Neighborhood_set");
                   // p.flush();
                }else{
                    os.writeObject("hash_map_type==none");
                    //p.flush();
                }
                
            os.writeObject(hashmap);
            //os.flush();
       }
       /*try(Scanner sc1=new Scanner(s.getInputStream())){
       remote_state= sc1.next();
       }*/
       return "true";
    }
    public String send_file_socket(String port_number,int msg_key,String msg_value) throws IOException, ClassNotFoundException{
        Socket s= port_number_to_socket(port_number);
        String remote_state;
       //Socket s = new Socket("127.0.0.1",port_number);
       try (ObjectOutputStream os = new ObjectOutputStream( new BufferedOutputStream(s.getOutputStream()))) {
                
                os.writeObject("msg_type==file");
                os.writeObject("file_key");
                os.writeObject(msg_key);
                os.writeObject("value");
                os.writeObject(msg_value);
                
                
        }
      // Scanner sc1=new Scanner(s.getInputStream());
       //remote_state= sc1.next();
       ObjectInputStream is = new ObjectInputStream( new BufferedInputStream(s.getInputStream()));
       remote_state= is.readObject().toString();
       return remote_state;
    }
    public String send_state_socket(String port_number) throws IOException, ClassNotFoundException{
       Socket s= port_number_to_socket(port_number);
       String remote_state;
       //Socket s = new Socket("127.0.0.1",port_number);
       try (ObjectOutputStream os = new ObjectOutputStream( new BufferedOutputStream(s.getOutputStream()))) {
                
                os.writeObject("msg_type==state");
                os.writeObject("node_ID");
                os.writeObject(this.host_ID);
                os.writeObject("State_value");
                os.writeObject(this.state);
                
        }
       /*Scanner sc1=new Scanner(s.getInputStream());
       remote_state= sc1.next();*/
       ObjectInputStream is = new ObjectInputStream( new BufferedInputStream(s.getInputStream()));
       remote_state= is.readObject().toString();
       return remote_state;
    }
    public String check_state(String port_number) throws IOException, ClassNotFoundException{
       //return socket_send_msg(port_number,"CS","your_state");
       String remote_state;
       //Socket s = new Socket("127.0.0.1",port_number);
       Socket s = port_number_to_socket(port_number);
       ObjectInputStream is = new ObjectInputStream( new BufferedInputStream(s.getInputStream()));
       ObjectOutputStream os = new ObjectOutputStream( new BufferedOutputStream(s.getOutputStream()));
       os.writeObject("msg_type==just_checking_you");
        
       /*Scanner sc1=new Scanner(s.getInputStream());
       remote_state= sc1.next();*/
       
       remote_state= is.readObject().toString();
       is.close();
       os.close();
       s.close();
       return remote_state;
    }
    public void change_state(){
        if(state){
            state=false;
        }else{
            state=true;
        }
    }
    public void listen_server() throws IOException, ClassNotFoundException, InterruptedException{
      String msg,temp;
      ServerSocket s1 = new ServerSocket(Integer.parseInt(this.host_IP));
      while(true){
        
        System.out.println("Doing update for :"+this.host_IP);
        
        System.out.println("update finiched for :"+this.host_IP);
        Socket ss=s1.accept();
        if(ss != null){
      //  Scanner sc=new Scanner(ss.getInputStream());
        System.out.println("the node is running in listening server :"+this.host_IP);
        
        ObjectInputStream is = new ObjectInputStream( new BufferedInputStream(ss.getInputStream()));
        ObjectOutputStream os = new ObjectOutputStream( new BufferedOutputStream(ss.getOutputStream()));
        //    if(is.available()>0){
                
         msg=is.readObject().toString();
       //     }else{
       // msg=sc.next();
            //}
        System.out.println("i: "+ this.host_IP+"received the following msg  :"+ msg);
        if("msg_type==just_checking_you".equals(msg)){
            os.writeObject(String.valueOf(this.state));
            System.out.println("i sent my state: "+this.host_IP);
        }if("msg_type==state".equals(msg)){
            int node_ID;
            String node_State;
            msg=is.readObject().toString();
            if(msg=="node_ID"){
                node_ID=Integer.parseInt(is.readObject().toString());
                msg=is.readObject().toString();
                if(msg=="State_value"){
                    node_State=is.readObject().toString();
                    update_for_specific_node(node_ID,node_State);
                    
                }
            }
        } if("msg_type==request_state_table".equals(msg)){
            msg=is.readObject().toString();
            System.out.println("next parameter  :"+ msg);
            if("node_IP".equals(msg)){
                String node_IP=is.readObject().toString();  
                System.out.println("i received table request from :"+ node_IP);
                answer_initialize_state_request(node_IP);
            }
        } if("msg_type==file".equals(msg)){
            int file_key;
            String file_value;
            msg=is.readObject().toString();
            if("file_key".equals(msg)){
                file_key=Integer.parseInt(msg=is.readObject().toString());
                msg=is.readObject().toString();
                if("value".equals(msg)){
                    file_value=is.readObject().toString();
                    deal_with_received_file(file_value,file_key);
                }
            }
            
        } if ("msg_type==answer_hashmap".equals(msg)){
            
            msg=is.readObject().toString();
           // msg=is.readObject().toString();
            String hash_map_type = null;
            if("hash_map_type==leaf_set".equals(msg)){
                hash_map_type="leaf_set";
            }else if("hash_map_type==routing_table".equals(msg)){
                hash_map_type="routing_table";
            }else if("hash_map_type==Neighborhood_set".equals(msg)){
                hash_map_type="Neighborhood_set";
            }
            System.out.println("and its about "+msg);
            
            HashMap<Integer,String> hashmap= (HashMap)is.readObject();
            
                  os.writeObject(this.state);
                  //System.out.println("Replied");
                 
              
            //ss.close();
            System.out.println("gooot iitt");
            receive_leaf_set_hashmap(hashmap);
            receive_routing_table_hashmap(hashmap);
            receive_Neighborhood_set_hashmap(hashmap);
            
        }
        
        
        os.writeObject(String.valueOf(this.state));
        is.close();
        ss.close();
        }
        //System.out.println("sleeping time for "+this.host_IP);
        //Thread.sleep(100);
        
        System.out.println("the node is running :"+this.host_ID);
      }
    }
    public String socket_send_msg(String dest_port_number,String msg_type,String msg) throws IOException, ClassNotFoundException{
        String remote_state;
         Scanner sc= new Scanner(System.in);
         Socket s = new Socket("127.0.0.1",Integer.parseInt(dest_port_number));
         //Scanner sc1=new Scanner(s.getInputStream());
         //System.out.println("Enter any number");

         ObjectOutputStream os = new ObjectOutputStream( new BufferedOutputStream(s.getOutputStream()));
         os.writeObject(host_name+msg_type+msg);
         //remote_state= sc1.next();
         ObjectInputStream is = new ObjectInputStream( new BufferedInputStream(s.getInputStream()));
         remote_state= is.readObject().toString();
         return remote_state;
    }
    public void deliver_file(String msg,int key){
        Others_files.put(key,msg);
    }
    public void deal_with_received_file(String msg,int key) throws IOException, ClassNotFoundException{
        int closest_node=select_closest_node_ID_to_key(key);
        if(closest_node==host_ID){
            deliver_file(msg,key);
        }else{
            send_file_socket(Integer.toString(closest_node),key,msg);
        }
    }
    public int select_closest_node_ID_to_key(int key){
        int distance=999999999,actual_closest_node=host_ID;


      if(get_max_ID_in_leaf_set()>key && key >get_min_ID_in_leaf_set()){
          if(key>this.host_ID){
          Set set_of_Leaf_set = Leaf_set_H.entrySet();
          Iterator iterator = set_of_Leaf_set.iterator();
          while(iterator.hasNext()){
               Map.Entry mentry = (Map.Entry)iterator.next();
               if(abs(Integer.valueOf((int)mentry.getKey())- key) < distance){
                distance=abs(Integer.valueOf((int)mentry.getKey())- key);
                actual_closest_node=Integer.valueOf((int)mentry.getKey());
                    }
                    //System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
                    //System.out.println(mentry.getValue());
               }
          }else{
                Set set_of_Leaf_set = Leaf_set_L.entrySet();
                Iterator iterator = set_of_Leaf_set.iterator();
                while(iterator.hasNext()){
                Map.Entry mentry = (Map.Entry)iterator.next();
                if(abs(Integer.valueOf((int)mentry.getKey())- key) < distance){
                distance=abs(Integer.valueOf((int)mentry.getKey())- key);
                actual_closest_node=Integer.valueOf((int)mentry.getKey());
                    }
                    //System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
                    //System.out.println(mentry.getValue());
               }
          }
      }else{
          Set set_of_Routing_table = Routing_table.entrySet();
          Iterator iterator = set_of_Routing_table.iterator(); 
          while(iterator.hasNext()){
               Map.Entry mentry = (Map.Entry)iterator.next();
               if(abs(Integer.valueOf((int)mentry.getKey())- key) < distance){
                  distance=abs(Integer.valueOf((int)mentry.getKey())- key);
                  actual_closest_node=Integer.valueOf((int)mentry.getKey());
               }
               //System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
               //System.out.println(mentry.getValue());
          }
      }
      return actual_closest_node;
    }
    public boolean route(String msg, int key) {
        
        return true;
    }
    public void send_socket_msg(String Dest_IP,String msg){
        String serverName = Dest_IP;
        int port = 8088;
        try
        {
           System.out.println("Connecting to " + serverName +" on port " + port);
           Socket client = new Socket(serverName, port);
           System.out.println("Just connected to " + client.getRemoteSocketAddress());

           OutputStream outToServer = client.getOutputStream();
           DataOutputStream out = new DataOutputStream(outToServer);
           out.writeUTF("Hello from "
                        + client.getLocalSocketAddress());
           InputStream inFromServer = client.getInputStream();
           DataInputStream in =
                          new DataInputStream(inFromServer);
           System.out.println("Server says " + in.readUTF());
           client.close();
        }catch(IOException e)
        {
           e.printStackTrace();
        }
    }
    public int get_max_ID_in_leaf_set(){
      int max=0;
      Set set = Leaf_set_H.entrySet();
      Iterator iterator = set.iterator();
      while(iterator.hasNext()){
           Map.Entry mentry = (Map.Entry)iterator.next();
           if(Integer.valueOf((int)mentry.getKey())>max){
              max=Integer.valueOf((int)mentry.getKey()); 
           }
           //System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
           //System.out.println(mentry.getValue());
      }
      this.max_Leafset=max;
      return max;
    }
    public int get_min_ID_in_leaf_set(){
      int min=0;
      Set set = Leaf_set_L.entrySet();
      Iterator iterator = set.iterator();
      while(iterator.hasNext()){
           Map.Entry mentry = (Map.Entry)iterator.next();
           if(Integer.valueOf((int)mentry.getKey())<min){
              min=Integer.valueOf((int)mentry.getKey()); 
           }
           //System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
           //System.out.println(mentry.getValue());
      }
      this.min_Leafset=min;
      return min;
    }
    public void Update_leaf_set_ALL() throws IOException, ClassNotFoundException{
      HashMap<Integer, String> Leaf_set = new HashMap();
      Leaf_set.putAll(this.Leaf_set_H);
      Leaf_set.putAll(this.Leaf_set_L);
      Set set = Leaf_set.entrySet();
      Iterator iterator = set.iterator();
      String state="true";
      while(iterator.hasNext()){
           Map.Entry mentry = (Map.Entry)iterator.next();
           state=check_state(mentry.getValue().toString());
           System.out.println("waaaaa333 ha ach kayn : " +state);
           if (state == "false" ){
               if(Integer.valueOf((int)mentry.getKey()) > this.host_ID){
               this.Leaf_set_H.remove(mentry.getKey());
               }else{
               this.Leaf_set_L.remove(mentry.getKey());    
               }
           }
      }
    }
    public void Update_Routing_table_set_ALL() throws IOException, ClassNotFoundException{
      Set set = this.Routing_table.entrySet();
      Iterator iterator = set.iterator();
      String state;
      while(iterator.hasNext()){
           Map.Entry mentry = (Map.Entry)iterator.next();
           state=check_state(mentry.getValue().toString());
           if ("false".equals(state) ){
               this.Routing_table.remove(mentry.getKey());
               
           }
      }
    }
    public void Update_Neighborhood_set_ALL() throws IOException, ClassNotFoundException{
      Set set = this.Neighborhood_set.entrySet();
      Iterator iterator = set.iterator();
      String state;
      while(iterator.hasNext()){
           Map.Entry mentry = (Map.Entry)iterator.next();
           state=check_state(mentry.getValue().toString());
           if ("false".equals(state) ){
               this.Neighborhood_set.remove(mentry.getKey());
               
           }
      }
    }
    public void receive_leaf_set_hashmap(HashMap<Integer,String> set_map){
        System.out.println(this.host_ID+": received a leaf set hashmap");
        Set set = this.Routing_table.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()){
                Map.Entry mentry = (Map.Entry)iterator.next();
               if( Integer.valueOf((int)mentry.getKey()) > this.host_ID){
                    if(this.Leaf_set_H.size()<L_2){
                        this.Leaf_set_H.put(Integer.valueOf((int)mentry.getKey()), set_map.get(Integer.valueOf((int)mentry.getKey())));
                   }else{
                        if(get_max_ID_in_leaf_set() > Integer.parseInt((String)mentry.getKey())){
                       this.Leaf_set_H.remove(get_max_ID_in_leaf_set());
                       this.Leaf_set_H.put(Integer.valueOf((int)mentry.getKey()), set_map.get(Integer.valueOf((int)mentry.getKey())));
                   
                        }
                    }
               }else{
                   if(this.Leaf_set_L.size()<L_2){
                       this.Leaf_set_L.put(Integer.valueOf((int)mentry.getKey()), set_map.get(Integer.valueOf((int)mentry.getKey())));
                   }else{
                        if(get_min_ID_in_leaf_set() < Integer.valueOf((int)mentry.getKey())){
                       this.Leaf_set_L.remove(get_max_ID_in_leaf_set());
                       this.Leaf_set_L.put(Integer.valueOf((int)mentry.getKey()), set_map.get(Integer.valueOf((int)mentry.getKey())));
                   
                        }
                    }
               }
            }
    }
    public void receive_routing_table_hashmap(HashMap<Integer,String> set_map){
        if(this.Routing_table.isEmpty()){
            this.Routing_table=set_map;
        }else{
            Set set = this.Routing_table.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()){
                Map.Entry mentry = (Map.Entry)iterator.next();
                int degree=calculte_matching_degree_with_node_ID(Integer.valueOf((int)mentry.getKey()));
                if(count_redandency_of_degree_in_routingtable(degree)<4){
                    this.Routing_table.put(Integer.valueOf((int)mentry.getKey()), set_map.get(Integer.valueOf((int)mentry.getKey())));
                }

            }
        }  
    }
    public void receive_Neighborhood_set_hashmap(HashMap<Integer,String> set_map){
        if(this.Neighborhood_set.isEmpty()){
            this.Neighborhood_set=set_map;
        }else{
            Set set = this.Routing_table.entrySet();
            Iterator iterator = set.iterator();
            int distance_actual=999999;
            int distance_received=999999;
            while(iterator.hasNext()){
                Map.Entry mentry = (Map.Entry)iterator.next();
                distance_actual=abs(Integer.parseInt(this.host_IP)- Integer.valueOf((int)mentry.getKey()));
                Set set_received= set_map.entrySet();
                if(!set_received.isEmpty()){
                Iterator iterator_received=set_received.iterator();
                int best_distance_received=999999;
                int best_distance_received_key=999999;
                while(iterator_received.hasNext()){
                    Map.Entry mentry_received = (Map.Entry)iterator_received.next();
                    distance_received=abs(Integer.parseInt(this.host_IP)- Integer.valueOf((int)mentry_received.getKey()));
                    if(distance_received < distance_actual){
                        if(distance_received < best_distance_received){
                            best_distance_received=distance_received;
                            best_distance_received_key=Integer.valueOf((int)mentry.getKey());
                        }
                    }
                }
                    if(best_distance_received !=  999999 && best_distance_received_key!= 999999 ){

                        this.Neighborhood_set.remove(Integer.valueOf((int)mentry.getKey()));
                        this.Neighborhood_set.put(best_distance_received_key, set_map.get(best_distance_received_key));

                    }
            }
            }
            
        } 
    }
    public void update_leaf_set_state(int node_ID,String state){
       if("false".equals(state) ){
           if(node_ID > this.host_ID){
               this.Leaf_set_H.remove(node_ID);
           }else{
               this.Leaf_set_L.remove(node_ID);
           }
       }
    }
    public void update_Neighborhood_set_state(int node_ID,String state){
       if("false".equals(state) ){
               this.Neighborhood_set.remove(node_ID);
       }
    }
    public void update_Routing_table_state(int node_ID,String state){
       if("false".equals(state) ){
               this.Routing_table.remove(node_ID);
       }
    }
    public void update_for_specific_node(int node_ID,String state){
        update_leaf_set_state(node_ID,state);
        update_Routing_table_state(node_ID,state);
        update_Neighborhood_set_state(node_ID,state);
    }
    public boolean check_matching_degree(int x , int degree){
        int a;
        int b;
        a=(int) (this.host_ID/(Math.pow(10,degree)));
        b=(int) (x/(Math.pow(10,degree)));
        
        if(a==b){
            return true;
        }else{
            return false;
        }
        
    }
    public Integer count_redandency_of_degree_in_routingtable(int degree){
        int counter=0;
        Set set = this.Routing_table.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()){
            Map.Entry mentry = (Map.Entry)iterator.next();
            if(check_matching_degree(Integer.valueOf((int)mentry.getKey()),degree)){
            counter++;
            }
        }
        return counter;
    }
    public Integer calculte_matching_degree_with_node_ID(int x){
        int degree=9;
        for(int i=6;i>0;i--){
            if (x/(Math.pow(10, i))   ==   this.host_ID/(Math.pow(10,i))){
                degree=i;
            }
        }
        return degree;
    }
    public void updater() throws IOException, ClassNotFoundException, InterruptedException{
        while(true){
            long tNow = System.currentTimeMillis();
            long tDelta = tNow - this.last_time_update;
            double elapsedSeconds = tDelta / 1000.0;
            if(elapsedSeconds > 120){
                System.out.println("doing update: "+this.host_IP);
                System.out.println(this.last_time_update);
                System.out.println(tNow);
                System.out.println(elapsedSeconds);
                Thread thread_Update_leaf_set_ALL = new Thread() {
                    public void run() {
                        try {
                            Update_leaf_set_ALL();
                        } catch (IOException ex) {
                            Logger.getLogger(Pastry_node.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Pastry_node.class.getName()).log(Level.SEVERE, null, ex);
                        } 
                    }
                };
                Thread thread_Update_Neighborhood_set_ALL = new Thread() {
                    public void run() {
                        try {
                            Update_Neighborhood_set_ALL();
                        } catch (IOException ex) {
                            Logger.getLogger(Pastry_node.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Pastry_node.class.getName()).log(Level.SEVERE, null, ex);
                        } 
                    }
                };
                Thread thread_Update_Routing_table_set_ALL = new Thread() {
                    public void run() {
                        try {
                            Update_Routing_table_set_ALL();
                        } catch (IOException ex) {
                            Logger.getLogger(Pastry_node.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Pastry_node.class.getName()).log(Level.SEVERE, null, ex);
                        } 
                    }
                };
                
                thread_Update_Neighborhood_set_ALL.start();
                thread_Update_leaf_set_ALL.start();
                thread_Update_Routing_table_set_ALL.start();
                
                this.last_time_update=System.currentTimeMillis();
            }
            
        }
    }
    Pastry_node(String _host_name,String _host_IP) throws IOException, ClassNotFoundException{
        this.host_name=_host_name;
        this.host_IP=_host_IP;
        this.host_ID = abs((_host_name+_host_IP).hashCode())/10000;
        this.Routing_table.put(this.host_ID, this.host_IP);
        this.Neighborhood_set.put(18001,"8081");
        this.Routing_table.put(18001,"8081");
        this.Leaf_set_L.put(18001,"8081");
        this.state=true;
        last_time_update=System.currentTimeMillis();
        System.out.println("it's ip is"+this.host_IP);
       
        /*System.out.println("the node is running :"+this.host_ID);
        if(this.host_IP != "8081"){
            initialize_state_request(Integer.toString(this.host_ID),port_number_to_socket("8081"));
        
        }
        listen_server();*/
       /* Thread a_thread= new Thread();
        a_thread.start();*/
        
    }
    
    public void run() {
       // try{
        System.out.println("the node is running in run methos :"+this.host_ID);
        if(!"8081".equals(this.host_IP)){
            try {
                initialize_state_request(Integer.toString(this.host_ID),port_number_to_socket("8081"));
            } catch (IOException ex) {
                Logger.getLogger(Pastry_node.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
            Thread thread_updater = new Thread() {
                    public void run() {
                        try {
                            updater();
                        } catch (IOException ex) {
                            Logger.getLogger(Pastry_node.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Pastry_node.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Pastry_node.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
            Thread thread_listen = new Thread() {
                    public void run() {
                        try {
                            listen_server();
                        } catch (IOException ex) {
                            Logger.getLogger(Pastry_node.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Pastry_node.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Pastry_node.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
            thread_updater.start();
            thread_listen.start();
            /*}catch(Exception e){
            System.out.println("machakil");
            }*/
       
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException
   {
       
   }

}
