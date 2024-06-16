package com.example.eureka;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MsscBreweryEurekaApplicationTests {

  @Test
  void contextLoads() {
    assertEquals("3.3.0", SpringBootVersion.getVersion());
  }

}
