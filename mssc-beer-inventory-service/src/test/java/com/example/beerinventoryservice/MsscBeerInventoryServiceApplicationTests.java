package com.example.beerinventoryservice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;

class MsscBeerInventoryServiceApplicationTests {

  @Test
  void contextLoads() {
    assertEquals("3.3.0", SpringBootVersion.getVersion());
  }

}
