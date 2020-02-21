package me.maiz.trainning.websocketdemo.websocket;

import lombok.Data;

@Data
public class Message {

    private String from;
    private String to;
    private String content;
}
