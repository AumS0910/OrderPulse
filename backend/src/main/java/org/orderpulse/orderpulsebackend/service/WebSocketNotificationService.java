package org.orderpulse.orderpulsebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orderpulse.orderpulsebackend.dto.OrderResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Broadcasts order updates to connected WebSocket clients.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyOrderUpdate(OrderResponse orderResponse) {
        log.info("Broadcasting order update via WebSocket: orderId={}", orderResponse.getId());
        messagingTemplate.convertAndSend("/topic/orders", orderResponse);
    }

    public void notifyUserOrderUpdate(String username, OrderResponse orderResponse) {
        messagingTemplate.convertAndSendToUser(username, "/queue/orders", orderResponse);
    }
}

