package com.coda.loadbalancer.controller;


import com.coda.loadbalancer.service.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@Slf4j(topic = "Route Controller")
@RequestMapping("/v1")
public class RouteController {

    @Autowired
    private RouteService routeService;


    @PostMapping(value = "/route", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> route(@RequestBody(required = true) Map<String, Object> payload) throws Exception{
        log.debug("echo request " + payload.toString());
        try {
            Map<?, ?> responseBody = routeService.route(payload);
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorDetails = constructErrorObject(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return ResponseEntity.internalServerError().body(errorDetails);
        }
    }

    @GetMapping(value = "/registerPod")
    public ResponseEntity<Object> registerPod(@RequestParam(required = true) String hostName, @RequestParam(required = true) String port) throws Exception{
        try{
            routeService.registerPod(hostName, port);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (Exception e) {
            Map<String, Object> errorDetails = constructErrorObject(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
        }
    }

    @GetMapping(value = "/loadTest")
    public ResponseEntity<Object> loadTest (@RequestParam(required = true) int totalRequest, @RequestParam(required = true) int totalConcurrent, @RequestParam(required = true) String url ) {
        log.info("Starting load test");
        Map<String, Object> error = new HashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(totalConcurrent);
        List<Future<Map<?,?>>> futureList = new ArrayList<>();
        if (totalRequest < totalConcurrent) {
            Map<String, Object> errorDetails = constructErrorObject(HttpStatus.INTERNAL_SERVER_ERROR, "totalRequest cannot be less than totalConcurrent");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
        }
        int rounds = totalRequest / totalConcurrent;
        for (int j= 0; j < rounds ; j++) {
            for(int i=0; i<totalConcurrent; i++) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("game", "Mobile Legends");
                payload.put("gamerID", "GYUTDTE");
                payload.put("points", 20);
                payload.put("loadTesting", true);
                Future<Map<?,?>> future = executor.submit(() -> routeService.sendRequestWithoutRetry(payload, url));
                futureList.add(future);
            }
        }

        for(Future<Map<?,?>> future: futureList) {
            try {
                future.get();
            } catch(Exception ex) {
                ex.printStackTrace();
                Map<String, Object> errorDetails = constructErrorObject(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
            }
        }

        log.info("Load test ended");
        return ResponseEntity.status(HttpStatus.OK).build();


    }

    public Map<String, Object> constructErrorObject(HttpStatus status, String cause) {
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("cause", cause);
            errorDetails.put("status", status.value());
            errorDetails.put("error", status.toString());
            errorDetails.put("timestamp", LocalDateTime.now());
            return errorDetails;
    }
}

