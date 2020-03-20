package com.skcc.zcp.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bean")
public class PropertiesConfig {

    private String data = "This is default data";

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

}
