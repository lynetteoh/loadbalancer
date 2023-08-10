package com.coda.loadbalancer.config;

//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableRetry
@EnableScheduling
public class AppConfig {

//    @Bean
//    public OpenAPI customOpenAPI(@Value("${application.description}") String appDesciption, @Value("${application.name}") String appName, @Value("${application.version}") String appVersion) {
//        return new OpenAPI()
//                .info(new Info().title(appName)
//                        .version(appVersion)
//                        .description(appDesciption));
//    }





}
