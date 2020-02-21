package me.maiz.trainning.websocketdemo.websocket;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/chat/{username}")
@Slf4j
@Component
public class ChatEndPoint {
    private static Gson gson = new Gson();

    //保存当前session
    private Session session;

    //保存一系列聊天端点
    private static Set<ChatEndPoint> chatEndpoints
            = new CopyOnWriteArraySet<>();

    //保存所有用户
    private static HashMap<String, String> users = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        log.info("连接");
        this.session = session;
        chatEndpoints.add(this);
        users.put(session.getId(), username);

        Message message = new Message();
        message.setFrom(username);
        message.setContent("Connected!");

        broadcast(message);
    }

    @OnMessage
    public void onMessage(Session session, String msg) throws IOException {
        // Handle new messages
        log.info("消息到达{}：{}",session.getId(),msg);
        Message message = new Message();
        message.setContent(msg);
        message.setFrom(users.get(session.getId()));
        broadcast(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        chatEndpoints.remove(this);
        Message message = new Message();
        message.setFrom(users.get(session.getId()));
        message.setContent("Disconnected!");
        broadcast(message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here

        System.err.println("连接异常：" + throwable.getMessage());
        throwable.printStackTrace();
    }

    //广播到各个端点
    private static void broadcast(Message message)
            throws IOException {

        chatEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote().
                            sendText(gson.toJson(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
