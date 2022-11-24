/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.net.*;
import java.io.*;
import java.lang.Exception.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Rawrg
 */
public class JServer {
    
    protected int PORT;
    protected final String HOST;
    protected MessageHolder messages;
    protected int connectionId;
    protected ArrayList<String> userNames;
    protected ArrayList<Integer> userID;
    protected ArrayList<Boolean> alertNewMessages;
    protected String password;
    
    public JServer(int port, String host, String password){
        PORT = port;
        HOST = host;
        this.password = password;
        connectionId = 0;
        userNames = new ArrayList<>();
        alertNewMessages = new ArrayList<>();
        userID = new ArrayList<>();
        messages = new MessageHolder(30);
        
    }
    private int getUserInfo(BufferedReader in, PrintWriter out) throws IOException{
        userNames.add(in.readLine());
        int id = getIdForUser();
        userID.add(id);
        alertNewMessages.add(true);
        messages.saveMessage(userNames.get(id) + " has connected. Hello " + userNames.get(id) + "!");
        return id;
    }
    private int getIdForUser(){
        int temp = connectionId;
        connectionId++;
        return temp;
    }
    private void newMessage(){
        for(int i = 0; i < alertNewMessages.size(); i++){
            alertNewMessages.set(i, Boolean.TRUE);
        }
    }
    
    void delegate(Socket clientSocket){
        int id;
        try(
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ){
            if(password.equals(in.readLine())){
                synchronized(this){
                    id = getUserInfo(in, out);
                }
                String[] messageList = messages.getMessages();
                for(String message : messageList){
                    if(message != null){
                        out.println(message);
                    }
                }       
                    alertNewMessages.set(id, Boolean.FALSE);
                
                while(clientSocket.isConnected()){
                    while(in.ready()){
                        synchronized(this){
                            while(in.ready()){
                                messages.saveMessage(userNames.get(id) + ": " + in.readLine());
                                 newMessage();
                            }
                        }
                    }
                    if(alertNewMessages.get(id) == true){
                        out.println(messages.getLastMessage());       
                        alertNewMessages.set(id, Boolean.FALSE);
                    }
                }
            }

        } catch (Exception e){
            System.out.println("Disconnection");
            System.err.println(e);
            System.exit(-1);
        }
    }
    
    public void serve() {
        
        try{
            InetAddress hostIP = InetAddress.getByName(HOST);
            ServerSocket serverSocket = new ServerSocket(PORT, 10, hostIP);
            while(true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    Runnable runnable = () -> this.delegate(clientSocket);
                    Thread t = new Thread(runnable);
                    t.start();
                } catch (Exception e) {
                    System.err.println(e);
                    System.exit(-7);
                }
            }
        } catch (IOException e) {
            System.err.println(e);
            System.exit(-2);
        } catch (SecurityException e) {
            System.err.println(e);
            System.exit(-3);
        } catch (IllegalArgumentException e) {
            System.err.println(e);
            System.exit(-4);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(-5);
        }
    }
}
