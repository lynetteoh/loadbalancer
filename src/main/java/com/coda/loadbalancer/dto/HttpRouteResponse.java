package com.coda.loadbalancer.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class HttpRouteResponse {
    private Map<Object, Object> body = new HashMap<>();
}
