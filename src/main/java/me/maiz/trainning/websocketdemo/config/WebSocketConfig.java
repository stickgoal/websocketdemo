package me.maiz.trainning.websocketdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter endpointExporter(){
        final ServerEndpointExporter serverEndpointExporter = new ServerEndpointExporter();

        return serverEndpointExporter;
    }

}
