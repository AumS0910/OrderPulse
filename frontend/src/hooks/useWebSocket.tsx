import { useEffect, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { OrderResponse } from "../types/api";

const wsBaseUrl = process.env.REACT_APP_WS_URL || "http://localhost:8080";

export const useWebSocket = (onOrderUpdate: (order: OrderResponse) => void) => {
  const [connected, setConnected] = useState(false);
  const clientRef = useRef<Client | null>(null);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS(`${wsBaseUrl}/ws`),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: () => {},
    });

    client.onConnect = () => {
      setConnected(true);
      client.subscribe("/topic/orders", (message) => {
        const order: OrderResponse = JSON.parse(message.body);
        onOrderUpdate(order);
      });
    };

    client.onStompError = () => {
      setConnected(false);
    };

    client.onWebSocketClose = () => {
      setConnected(false);
    };

    client.activate();
    clientRef.current = client;

    return () => {
      clientRef.current?.deactivate();
    };
  }, [onOrderUpdate]);

  return { connected };
};

