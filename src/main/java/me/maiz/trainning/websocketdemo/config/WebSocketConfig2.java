package me.maiz.trainning.websocketdemo.config;

import me.maiz.trainning.websocketdemo.mvc.SocketHandler;
import me.maiz.trainning.websocketdemo.mvc.WebSocketHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;
import org.springframework.web.socket.handler.LoggingWebSocketHandlerDecorator;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig2 implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //使用spring自带的装饰器添加日志和异常处理功能
        final LoggingWebSocketHandlerDecorator webSocketHandler = new LoggingWebSocketHandlerDecorator(new ExceptionWebSocketHandlerDecorator(new SocketHandler()));

        webSocketHandlerRegistry.
                addHandler(webSocketHandler, "/chat2")
                //将HTTP Session中的内容传递给WS的Session
                // 也可以自定义继承该Interceptor实现一些session操作，比如将username放入session中
                .addInterceptors(new WebSocketHandshakeInterceptor());
    }

}
