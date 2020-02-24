package me.maiz.trainning.websocketdemo.mvc;

import lombok.Data;

@Data
public class ChatMessage {

    private String from;

    private String to;

    private String op;

    private String messageType;

    private Object content;

}

