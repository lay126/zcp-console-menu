package com.skcc.zcp.controller;

import com.skcc.zcp.entity.PropertiesConfig;
import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/test")
public class RestController {
    @Autowired
    private PropertiesConfig yamlConfig;
//    @Autowired
//    private DiscoveryClient discoveryClient;

    @GetMapping("/data")
    public String load() {
        return String.format(yamlConfig.getData(), "", "");
    }

}
