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
    
    protected int port;
    protected final String HOST = "";
    protected MessageHolder messages;
    protected int connectionId;
    protected ArrayList<String> userNames;
    protected ArrayList<Boolean> alertNewMessages;
    
    public JServer(int port){
        this.port = port;
        connectionId = 0;
        userNames = new ArrayList<>();
        alertNewMessages = new ArrayList<>();
        messages = new MessageHolder(30);
    }
    private String getUserName(BufferedReader in, PrintWriter out){
        return "";
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
        int id = getIdForUser();
        alertNewMessages.add(true);
        try(
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ){
            
            while(clientSocket.isConnected()){
                while(in.ready()){
                    synchronized(this){
                        while(in.ready()){
                            messages.saveMessage(in.readLine());
                             newMessage();
                        }
                    }
                    
                }
                if(alertNewMessages.get(id) == true){
                    String[] messageList = messages.getMessages();
                    for(String message : messageList){
                        if(message != null){
                            out.println(message);
                        }
                    }       
                    alertNewMessages.set(id, Boolean.FALSE);
                }
            }
            System.out.println("Client Disconnected nicely");
        } catch (Exception e){
            System.out.println("Disconnection");
            System.err.println(e);
            System.exit(-1);
        }
    }
    
    public void serve() {
        try(
            ServerSocket serverSocket = new ServerSocket(port);
        ) {
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
