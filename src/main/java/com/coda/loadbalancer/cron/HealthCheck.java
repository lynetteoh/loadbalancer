package com.coda.loadbalancer.cron;

import com.coda.loadbalancer.util.GlobalRouteUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@DependsOn({"globalRouteUtil"})
public class HealthCheck {

    @Autowired
    private GlobalRouteUtil globalRouteUtil;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void onStartup() {
        this.execute();
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void onSchedule() {
        //health check endpoint
        this.execute();

    }

    public void execute() {
        try{
            Map<String, Boolean> activePod = new HashMap<>(GlobalRouteUtil.activePod);
            List<String> inactivePod = new ArrayList<>();
            for(String url: activePod.keySet()){
                try {
                    ResponseEntity<Object> responseEntity=restTemplate.getForEntity(url + "/actuator/health", Object.class);
                    if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                        inactivePod.add(url);
                    }
                } catch (Exception e) {
                    inactivePod.add(url);
                }
            }

            for (String podUrl: inactivePod) {
                    globalRouteUtil.removeInactiveHost(podUrl);
            }
            log.debug("Inactive pods : " +  inactivePod.toString());
        } catch (Exception e) {
            log.error("Error while doing health check: ", e);
        }


    }
}
