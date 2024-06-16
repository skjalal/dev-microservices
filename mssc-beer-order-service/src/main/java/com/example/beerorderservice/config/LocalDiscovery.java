package com.example.beerorderservice.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile(value = {"local"})
@Configuration
@EnableDiscoveryClient
public class LocalDiscovery {

}
