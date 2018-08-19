package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class Proxy {

    private Boolean enabled;
    private String url;
    private Integer port;

    public Boolean getEnabled() {
        return enabled;
    }


    public Integer getPort() {
        return port;
    }


    public void setPort(Integer port) {
        this.port = port;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

}
