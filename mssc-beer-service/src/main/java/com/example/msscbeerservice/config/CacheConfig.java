package com.example.msscbeerservice.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

}
