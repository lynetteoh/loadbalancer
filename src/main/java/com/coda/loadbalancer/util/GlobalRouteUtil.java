package com.coda.loadbalancer.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class GlobalRouteUtil {

    @Autowired
    private RestTemplate restTemplate;

    // keep track of hosts
    public static Map<String, Boolean> activePod = new ConcurrentHashMap<>();
    // keep track of sequence
    public static LinkedList<String> queue = new LinkedList<>();

    public void tempRemoveInactiveHost(String hostName) throws Exception{
        if (activePod.containsKey(hostName)) {
            activePod.put(hostName, false);
        }
    }

    public void removeInactiveHost(String hostName) throws Exception{
        activePod.remove(hostName);
    }
    
    public void addNewHost(String hostName) throws Exception {
        activePod.put(hostName, true);
        queue.add(hostName);

    }

    public String getActivePod() {
        String host = "";
        for (int i = 0; i < queue.size(); i++) {
            host = queue.pop();
            // check if pop is active
            // if active, add back to queue
            if (activePod.containsKey(host)) {
                if (activePod.get(host)) {
                    queue.add(host);
                    break;
                }
            }
        }

        if (!StringUtils.isEmpty(host)) {
            // health check host again to make sure it is up
            try {
                log.debug ("health check validation for: " + host);
                ResponseEntity<Object> responseEntity=restTemplate.getForEntity(host + "/actuator/health", Object.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                    activePod.put(host, false);
                    host = getActivePod();
                }
            } catch (Exception e) {
                activePod.put(host, false);
            }
        }

        return host;

    }
}
