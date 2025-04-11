package dev.boenkkk.simulator_outstation_dnp3_dynamic.socket;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.model.MeasurementModel;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.service.MeasurementService;

import java.util.List;

import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MeasurementSocket {

    private final SocketIONamespace measurementNamespace;
    private final SocketIOService socketIOService;
    private final MeasurementService measurementService;

    public MeasurementSocket(SocketIONamespace measurementNamespace, SocketIOService socketIOService, MeasurementService measurementService) {
        this.measurementNamespace = measurementNamespace;
        this.socketIOService = socketIOService;
        this.measurementService = measurementService;
    }

    @PostConstruct
    public void init() {
        measurementNamespace.addConnectListener(onConnected());
        measurementNamespace.addDisconnectListener(onDisconnected());
        measurementNamespace.addEventListener("get-all", Void.class, getAll());
    }

    @OnConnect
    private ConnectListener onConnected() {
        return client -> {
            try {
                client.joinRoom("DEFAULT");

                String message = "Connected to socket";
                log.info("message:{}, client:{}", message, client.getSessionId().toString());
            } catch (Exception e) {
                log.error("error:{}", e.getMessage());
            }
        };
    }

    @OnDisconnect
    private DisconnectListener onDisconnected() {
        return client -> {
            try {
                String message = "Disconnected to socket";
                log.info("message:{}, client:{}", message, client.getSessionId().toString());
            } catch (Exception e) {
                log.error("error:{}", e.getMessage());
            }
        };
    }

    private DataListener<Void> getAll() {
        return (client, data, ackSender) -> {
            try {
                String nameSpace = client.getNamespace().getName();
                log.info("namespace: {}, data: {}", nameSpace, data);

                List<MeasurementModel> measurementModels = measurementService.getAll();
                socketIOService.sendMessageSelf(client, "listen", measurementModels);

                log.info("namespace:{}, total data:{}", nameSpace, measurementModels.size());
            } catch (Exception e) {
                socketIOService.sendMessageSelf(client, "listen", e.getMessage());
                log.error("error:{}", e.getMessage());
            }
        };
    }
}
