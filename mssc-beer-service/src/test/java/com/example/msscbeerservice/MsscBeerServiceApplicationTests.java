package com.example.msscbeerservice;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.apache.activemq.artemis.core.server.ActiveMQServers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MsscBeerServiceApplicationTests {

  private static final Logger log = LoggerFactory.getLogger("MsscBeerServiceApplicationTests");
  private static final WireMockServer wireMockServer = new WireMockServer(7777);
  private static ActiveMQServer server;

  @BeforeAll
  static void beforeAll() throws Exception {
    server = ActiveMQServers.newActiveMQServer(new ConfigurationImpl().setPersistenceEnabled(false)
        .setJournalDirectory("target/data/journal").setSecurityEnabled(false)
        .addAcceptorConfiguration("tcp", "tcp://127.0.0.1:61619"));

    server.start();
    log.info("JMS server started");
    wireMockServer.start();
    WireMock.configureFor("localhost", 7777);

    stubFor(get(urlPathTemplate("/api/v1/beer/{beerId}/inventory")).withPathParam("beerId",
        equalTo("0a818933-087d-47f2-ad83-2f986ed087eb")).willReturn(
        aResponse().withStatus(200).withHeader("Content-Type", "application/json")
            .withBody("[{\"quantityOnHand\":1}, {\"quantityOnHand\": 3}]")));
  }

  @AfterAll
  static void afterAll() throws Exception {
    if (server != null) {
      server.stop();
    }

    if (wireMockServer.isRunning()) {
      wireMockServer.stop();
      await().untilFalse(new AtomicBoolean(wireMockServer.isRunning()));
    }
  }

  @Test
  void contextLoads() {
    assertEquals("3.3.0", SpringBootVersion.getVersion());
  }

}
