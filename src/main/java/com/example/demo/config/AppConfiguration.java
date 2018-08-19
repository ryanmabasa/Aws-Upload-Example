package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "datasource")
public class AppConfiguration {

    private String access_key_id;
    private String secret_access_key;
    private String bucket_name;
    private String client_region;

	public String getAccess_key_id() {
		return access_key_id;
	}


	public String getClient_region() {
		return client_region;
	}

	public void setClient_region(String client_region) {
		this.client_region = client_region;
	}

	public String getBucket_name() {
		return bucket_name;
	}

	public void setBucket_name(String bucket_name) {
		this.bucket_name = bucket_name;
	}

	public String getSecret_access_key() {
		return secret_access_key;
	}

	public void setSecret_access_key(String secret_access_key) {
		this.secret_access_key = secret_access_key;
	}

	public void setAccess_key_id(String access_key_id) {
		this.access_key_id = access_key_id;
	}

    @NestedConfigurationProperty
	private Proxy proxy;


	public Proxy getProxy() {
		return proxy;
	}


	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}


}
