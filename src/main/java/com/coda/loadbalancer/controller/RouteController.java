package com.coda.loadbalancer.controller;


import com.coda.loadbalancer.dto.HttpRouteResponse;
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

import java.util.Map;

@RestController
@Slf4j(topic = "Route Controller")
@RequestMapping("/v1")
public class RouteController {

    @Autowired
    private RouteService routeService;


    @PostMapping(value = "/route", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> route(@RequestBody(required = true) Map<String, Object> payload) {
        log.debug("echo request " + payload.toString());
        try {
            Map<?, ?> responseBody = routeService.route(payload);
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e);
        }
    }

    @GetMapping(value = "/registerPod")
    public ResponseEntity<Object> registerPod(@RequestParam(required = true) String hostName, @RequestParam(required = true) String port) {
        try{
            routeService.registerPod(hostName, port);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex);
        }
    }
}

