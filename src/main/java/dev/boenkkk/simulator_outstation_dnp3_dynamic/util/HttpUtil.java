package dev.boenkkk.simulator_outstation_dnp3_dynamic.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpUtil {

    public String getIpAddress(String netInterface) {
        String ipAddress = null;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.getName().equalsIgnoreCase(netInterface)) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                            ipAddress = inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ipAddress;
    }
}
