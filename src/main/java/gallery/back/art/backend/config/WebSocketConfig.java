package gallery.back.art.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 엔드포인트 등록
        // 연결할 소켓 엔드포인트를 지정하는 코드
        registry.addEndpoint("/ws") // 연결될 엔드포인트
                .setAllowedOriginPatterns("*")
                .withSockJS(); // SocketJS 를 연결한다는 설정
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메세지브로커를 등록하는 코드
        // 보통 /topic 과 /queue 를 사용,
        // /topic 은 한명이 message 를 발행했을 때 해당 토픽을 구독하고 있는 n명에게 메세지를 뿌려야 하는 경우에 사용
        // /queue 는 한명이 message 를 발행했을 때 발행한 한 명에게 다시 정보를 보내는 경우에 사용
        registry.enableSimpleBroker("/topic", "/queue");

        // 도착경로에 대한 prefix 를 설정
        // registry.setApplicationDestinationPrefixes("/app"); 이라고 설정해두면
        // /topic/hello 라는 토픽에 대해 구독을 신청했을 때 실제 경로는 /app/topic/hello
        registry.setApplicationDestinationPrefixes("/");
    }
}
