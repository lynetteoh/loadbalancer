package com.coda.loadbalancer.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class GlobalRouteUtil {

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

    }

    public String getActivePod() {
        String host = "";
        for (int i = 0; i < queue.size(); i++) {
            host = queue.pop();
            // check if pop is actibe
            // if active, add back to queue
            if (activePod.containsKey(host)) {
                if (activePod.get(host)) {
                    queue.add(host);
                    break;
                }
            }
        }
        return host;

    }
}
