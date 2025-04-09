package dev.boenkkk.simulator_outstation_dnp3_dynamic.service.callback;

import dev.boenkkk.simulator_outstation_dnp3_dynamic.properties.Dnp3Properties;
import dev.boenkkk.simulator_outstation_dnp3_dynamic.util.HttpUtil;

import org.springframework.stereotype.Component;

import io.stepfunc.dnp3.ConnectionState;
import io.stepfunc.dnp3.ConnectionStateListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ConnectionStateListenerImpl implements ConnectionStateListener {

    private final Dnp3Properties dnp3Properties;
    private final HttpUtil httpUtil;

    public ConnectionStateListenerImpl(Dnp3Properties dnp3Properties, HttpUtil httpUtil) {
        this.dnp3Properties = dnp3Properties;
        this.httpUtil = httpUtil;
    }

    @Override
    public void onChange(ConnectionState connectionState) {
        try {
            int stateInt = switch (connectionState.name().toUpperCase()) {
                case "CONNECTED" -> 1;
                case "DISCONNECTED" -> 0;
                default -> 2;
            };

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonNode = objectMapper.createObjectNode();
            jsonNode.put("server_name", dnp3Properties.getOutstationName());
            jsonNode.put("ip_address", httpUtil.getIpAddress(dnp3Properties.getOutstationNetworkInterface()));
            jsonNode.put("protocol", dnp3Properties.getOutstationProtocol());
            jsonNode.put("status", stateInt);
            jsonNode.put("status_name", connectionState.name());
            jsonNode.put("time", System.currentTimeMillis());
            String json = objectMapper.writeValueAsString(jsonNode);

            log.info(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
