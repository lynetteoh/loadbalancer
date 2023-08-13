package com.coda.loadbalancer.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestClientImpl {
    @Autowired
    private Environment environment;

    @Bean
    public RestTemplate getRestTemplate() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory  = new SimpleClientHttpRequestFactory();
        if (!StringUtils.isEmpty(environment.getProperty("http.connect.timeout"))) {
            clientHttpRequestFactory.setConnectTimeout(Integer.parseInt(environment.getProperty("http.connect.timeout")));
        }
        if (!StringUtils.isEmpty(environment.getProperty("http.read.timeout"))) {
            clientHttpRequestFactory.setReadTimeout(Integer.parseInt(environment.getProperty("http.read.timeout")));
        }
        return new RestTemplate(clientHttpRequestFactory);
    }
}
