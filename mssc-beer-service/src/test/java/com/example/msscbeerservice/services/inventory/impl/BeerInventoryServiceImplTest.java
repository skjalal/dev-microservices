package com.example.msscbeerservice.services.inventory.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.msscbeerservice.services.inventory.BeerInventoryService;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {BeerInventoryServiceImpl.class, RestTemplateAutoConfiguration.class})
class BeerInventoryServiceImplTest {

  private static final WireMockServer wireMockServer = new WireMockServer(7777);

  @Autowired
  BeerInventoryService beerInventoryService;

  @BeforeAll
  static void beforeAll() {
    wireMockServer.start();
    WireMock.configureFor("localhost", wireMockServer.port());
  }

  @AfterAll
  static void afterAll() {
    if (wireMockServer.isRunning()) {
      wireMockServer.stop();
      await().untilFalse(new AtomicBoolean(wireMockServer.isRunning()));
    }
  }

  @Test
  void getOnHandInventory() {
    UUID id = UUID.randomUUID();
    stubFor(WireMock.get(urlPathTemplate("/api/v1/beer/{beerId}/inventory"))
        .withPathParam("beerId", equalTo(id.toString())).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                .withBody("[{\"quantityOnHand\":1}, {\"quantityOnHand\": 3}]")));
    Integer onHandInventory = beerInventoryService.getOnHandInventory(id);
    assertNotNull(onHandInventory);
    assertTrue(onHandInventory > 0);
    assertEquals(4, onHandInventory);
  }
}