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
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Slf4j
public class GlobalRouteUtil {

    @Autowired
    private RestTemplate restTemplate;

    // keep track of hosts
    public static Map<String, Boolean> activePod = new ConcurrentHashMap<>();
    // keep track of sequence
    public static ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

    // temporary remove host so that no request will go to this host
    public void tempRemoveInactiveHost(String hostName) throws Exception{
        if (activePod.containsKey(hostName)) {
            activePod.put(hostName, false);
        }
    }

    // permanently remove host
    public void removeInactiveHost(String hostName) throws Exception{
        activePod.remove(hostName);
    }

    public void addNewHost(String hostName) throws Exception {
        activePod.put(hostName, true);
        queue.add(hostName);

    }

    // find pod that is healthy
    public String getActivePod() {
        String host = "";
        for (int i = 0; i < queue.size(); i++) {
            host = queue.poll();
            // check if pop is active
            // if active, add back to queue
            if (activePod.containsKey(host)) {
                if (activePod.get(host)) {
                    queue.add(host);
                    break;
                } else {
                    // ignore inactive host
                    host = "";
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
                log.error("health check failed for: " + host);
                activePod.put(host, false);
            }
        }

        return host;

    }
}
