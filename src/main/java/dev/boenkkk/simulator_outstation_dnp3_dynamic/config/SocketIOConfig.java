package dev.boenkkk.simulator_outstation_dnp3_dynamic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class SocketIOConfig {

    @Value("${socket-server.host}")
    private String host;

    @Value("${socket-server.port}")
    private Integer port;

    private SocketIOServer socketIOServer;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(port);

        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);
        config.setSocketConfig(socketConfig);

        // Initialize and start the Socket.IO server
        socketIOServer = new SocketIOServer(config);
        try {
            socketIOServer.start();
            log.info("Socket.IO server started on {}:{}", host, port);

            // Add shutdown hook to ensure the server stops on application exit
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (socketIOServer != null) {
                    log.info("Shutdown Hook: Stopping Socket.IO server...");
                    socketIOServer.stop();
                    log.info("Shutdown Hook: Socket.IO server stopped.");
                }
            }));

            return socketIOServer;
        } catch (Exception e) {
            log.error("Error starting Socket.IO server: {}", e.getMessage());
            throw e;
        }
    }

    // Graceful shutdown of Socket.IO server
    @PreDestroy
    public void stopSocketIOServer() {
        if (socketIOServer != null) {
            log.info("PreDestroy: Stopping Socket.IO server...");
            socketIOServer.stop();
            log.info("PreDestroy: Socket.IO server stopped.");
        }
    }

    @Bean
    public SocketIONamespace circuitBreakerNamespace(SocketIOServer server) {
        return server.addNamespace("/circuit-breaker");
    }

    @Bean
    public SocketIONamespace tapChangerNamespace(SocketIOServer server) {
        return server.addNamespace("/tap-changer");
    }

    @Bean
    public SocketIONamespace measurementNamespace(SocketIOServer server) {
        return server.addNamespace("/measurement");
    }
}
