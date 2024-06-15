package com.example.beerorderservice.services.beer.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.example.beerorderservice.services.beer.BeerService;
import com.example.brewery.models.BeerDTO;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {BeerServiceImpl.class, RestTemplateAutoConfiguration.class})
class BeerServiceImplTest {

  private static final Logger log = LoggerFactory.getLogger(BeerServiceImplTest.class);
  private static final WireMockServer wireMockServer = new WireMockServer(9999);

  String json;

  @Autowired
  BeerService beerService;

  @BeforeAll
  static void beforeAll() {
    if (wireMockServer.isRunning()) {
      log.debug("WireMock server already running");
    } else {
      wireMockServer.start();
    }
    WireMock.configureFor("localhost", 9999);
  }

  @AfterAll
  static void afterAll() {
    if (wireMockServer.isRunning()) {
      wireMockServer.stop();
      await().untilFalse(new AtomicBoolean(wireMockServer.isRunning()));
    }
  }

  @BeforeEach
  void setUp() {
    json = """
        {
          "beerName": "Test",
          "beerStyle": "ALA",
          "version": 1,
          "quantityOnHand": 2,
          "upc": "00002323423",
          "price": 123123.989,
          "id": "e533cfe3-5ead-48be-90c2-b93bef48826f",
          "beerId": "e533cfe3-5ead-48be-90c2-b93bef48826f"
        }
        """;
  }

  @Test
  void getBeerById() {
    UUID id = UUID.fromString("e533cfe3-5ead-48be-90c2-b93bef48826f");
    stubFor(get(urlPathTemplate("/api/v1/beer/{beerId}")).withPathParam("beerId", equalTo("e533cfe3-5ead-48be-90c2-b93bef48826f"))
        .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
            .withBody(json)));
    Optional<BeerDTO> beerDTO = beerService.getBeerById(id);
    assertFalse(beerDTO.isEmpty());
  }

  @Test
  void getBeerByUpc() {
    stubFor(get(urlPathTemplate("/api/v1/beer/upc/{upc}")).withPathParam("upc", equalTo("0006643432"))
        .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
            .withBody(json)));
    Optional<BeerDTO> beerDTO = beerService.getBeerByUpc("0006643432");
    assertFalse(beerDTO.isEmpty());
  }
}