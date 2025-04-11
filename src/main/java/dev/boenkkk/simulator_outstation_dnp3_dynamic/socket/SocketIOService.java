package dev.boenkkk.simulator_outstation_dnp3_dynamic.socket;

import org.springframework.stereotype.Service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SocketIOService {

    public final String DEFAULT_ROOM = "DEFAULT";

    private final SocketIONamespace circuitBreakerNamespace;
    private final SocketIONamespace tapChangerNamespace;
    private final SocketIONamespace measurementNamespace;

    public SocketIOService(SocketIONamespace circuitBreakerNamespace, SocketIONamespace tapChangerNamespace, SocketIONamespace measurementNamespace) {
        this.circuitBreakerNamespace = circuitBreakerNamespace;
        this.tapChangerNamespace = tapChangerNamespace;
        this.measurementNamespace = measurementNamespace;
    }

    private SocketIONamespace getNamespace(String namespace) {
        switch (namespace) {
            case "/circuit-breaker" -> {
                return circuitBreakerNamespace;
            }
            case "/tap-changer" -> {
                return tapChangerNamespace;
            }
            case "/measurement" -> {
                return measurementNamespace;
            }
            default -> {
                return null;
            }
        }
    }

    public void sendMessageSelf(SocketIOClient client, String eventName, Object message) {
        if (client != null) {
            client.sendEvent(eventName, message);
        }
    }

    public void broadcastToAllRoomsInNamespace(String namespace, String eventName, Object message) {
        SocketIONamespace ns = getNamespace(namespace);
        if (ns != null) {
            ns.getBroadcastOperations().sendEvent(eventName, message);
            log.info("Broadcasting to all rooms in namespace: {}|{}", eventName, namespace);
        }
    }

    public void broadcastToNamespace(String namespace, String room, String eventName, Object message) {
        SocketIONamespace ns = getNamespace(namespace);
        if (ns != null) {
            ns.getRoomOperations(room).sendEvent(eventName, message);
            log.info("Broadcasting: {}|{}|{}|{}", eventName, room, namespace, message);
        }
    }

    public void broadcastToDefaultRoom(String namespace, String eventName, Object message) {
        SocketIONamespace ns = getNamespace(namespace);
        if (ns != null) {
            ns.getRoomOperations(DEFAULT_ROOM).sendEvent(eventName, message);
            log.info("Broadcasting: {}|{}|{}|{}", eventName, DEFAULT_ROOM, namespace, message);
        }
    }
}
