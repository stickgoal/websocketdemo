package me.maiz.trainning.websocketdemo.mvc;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class SocketHandler extends TextWebSocketHandler {

    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private static Gson gson = new Gson();

    public List<String> getUsernameList(){
        return new ArrayList<>(sessions.keySet());
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("接收到消息 {}:{}", session.getId(), message.getPayload());
        final String payload = message.getPayload();

        ChatMessage chatMessage = gson.fromJson(payload, ChatMessage.class);
        chatMessage.setFrom(getUsername(session));
        if ("group".equals(chatMessage.getMessageType())) {
            broadcast(chatMessage);
        } else if ("single".equals(chatMessage.getMessageType())) {
            sendTo(chatMessage);
        }

    }

    private String getUsername(WebSocketSession session){
        final Object username = session.getAttributes().get("username");
        if (username == null) {
            throw new IllegalStateException("用户未登录");
        }
        return (String) username;
    }

    //单聊
    private void sendTo(ChatMessage chatMessage) throws IOException {
        String user = chatMessage.getTo();

        final WebSocketSession session = sessions.get(user);
        if(session==null){
            throw new IllegalStateException("用户"+user+"不存在");
        }

        session.sendMessage(new TextMessage(gson.toJson(chatMessage)));

    }

    //群聊
    private void broadcast(ChatMessage chatMessage) throws IOException {
        for (WebSocketSession s :
                sessions.values()) {
            if(s.isOpen()) {
                s.sendMessage(new TextMessage(gson.toJson(chatMessage)));
            }
        }

    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("建立链接：{}", session.getId());

        final String username = getUsername(session);
        log.info("用户名：{}", username);

        //将用户加入用户列表
        sessions.put( username, session);

        //广播加入信息
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageType("manage");
        chatMessage.setOp("join");
        chatMessage.setContent(sessions.keySet());
        broadcast(chatMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("链接{}退出",session.getId());

        String username = getUsername(session);

        //广播离开信息
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageType("manage");
        chatMessage.setOp("left");
        chatMessage.setContent(sessions.keySet());
        broadcast(chatMessage);

        sessions.remove(username);

    }
}
