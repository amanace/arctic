package com.acrtic.chat.model;

public class ChatMessage {
    public String type; // meassage | typing | status
     public Long messageId; 
    public String from;
    public String to;
    public String content;
    public boolean typing;
    public String status;
}
