/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Rawrg
 */
public class MessageHolder {
    private String[] messages;
    
    public MessageHolder(int size){
        messages = new String[size];
    }
    
    public void saveMessage(String message){
        for(int i = 0; i < messages.length; i++){
            messages[i] = messages[i+1];
        }
        messages[messages.length -1] = message;
    }
    
    
    public String[] getMessages(){
        return messages;
    }
    
}
