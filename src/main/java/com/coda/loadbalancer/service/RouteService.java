package com.coda.loadbalancer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.coda.loadbalancer.util.GlobalRouteUtil;
import java.util.Map;

@Service
@Slf4j(topic = "Route Service")
public class RouteService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GlobalRouteUtil globalRouteUtil;



    public Map<?, ?> loadBalance(Map<String, Object> payload) throws Exception{
        String host = globalRouteUtil.getActivePod();
        if (StringUtils.isEmpty(host)){
            log.error("Error: No active pod found! ");
            throw new RuntimeException("No active pod found");
        }

        try{
            return sendRequest(payload);
        } catch(HttpServerErrorException e) {
            globalRouteUtil.tempRemoveInactiveHost(host);
            throw e;
        }

    }

    public void registerPod(String hostName, String port) throws Exception{
        String podUrl = hostName + ":" + port;
        globalRouteUtil.addNewHost(podUrl);
    }


    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    private Map<?,?> sendRequest(Map<String, Object> payload) throws Exception {
        String url = globalRouteUtil.getActivePod() + "/echo";
        return restTemplate.postForObject(url, payload, Map.class);
    }

    @Recover
    public void retryExhaustHandling(HttpServerErrorException e) {
        // Log the error
        log.error("Unable to reach host: ", e);
        throw e;
    }
}
