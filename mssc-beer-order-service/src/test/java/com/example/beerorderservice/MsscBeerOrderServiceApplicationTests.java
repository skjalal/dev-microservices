package com.example.beerorderservice;

import static com.example.beerorderservice.config.JmsConfig.ALLOCATE_FAILURE_QUEUE;
import static com.example.beerorderservice.config.JmsConfig.DEALLOCATE_ORDER_QUEUE;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.beerorderservice.domain.BeerOrder;
import com.example.beerorderservice.domain.BeerOrderLine;
import com.example.beerorderservice.domain.BeerOrderStatusEnum;
import com.example.beerorderservice.domain.Customer;
import com.example.beerorderservice.repositories.BeerOrderRepository;
import com.example.beerorderservice.repositories.CustomerRepository;
import com.example.beerorderservice.services.BeerOrderManager;
import com.example.brewery.events.AllocationFailureEvent;
import com.example.brewery.events.DeAllocateOrderRequest;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.apache.activemq.artemis.core.server.ActiveMQServers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

@SpringBootTest
class MsscBeerOrderServiceApplicationTests {

  private static final Logger log = LoggerFactory.getLogger("MsscBeerOrderServiceApplicationTests");
  private static final WireMockServer wireMockServer = new WireMockServer(9999);
  private static ActiveMQServer server;

  Customer testCustomer;
  UUID beerId;

  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  BeerOrderManager beerOrderManager;

  @Autowired
  BeerOrderRepository beerOrderRepository;

  @Autowired
  JmsTemplate jmsTemplate;

  @BeforeAll
  static void beforeAll() throws Exception {
    server = ActiveMQServers.newActiveMQServer(new ConfigurationImpl().setPersistenceEnabled(false)
        .setJournalDirectory("target/data/journal").setSecurityEnabled(false)
        .addAcceptorConfiguration("tcp", "tcp://127.0.0.1:61619"));

    server.start();
    log.info("JMS server started");
    wireMockServer.start();
    WireMock.configureFor("localhost", wireMockServer.port());

    stubFor(get(urlPathTemplate("/api/v1/beer/upc/{upc}")).withPathParam("upc",
        equalTo("0631234200036")).willReturn(
        aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(
            "{\"id\": \"0a818933-087d-47f2-ad83-2f986ed087eb\", \"upc\":\"0631234200036\", \"quantityOnHand\":1}")));
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

  @BeforeEach
  void setUp() {
    beerId = UUID.fromString("0a818933-087d-47f2-ad83-2f986ed087eb");
    testCustomer = customerRepository.save(
        Customer.builder().customerName("Test Customer").build());
  }

  @Test
  void testNewToAllocated() {
    BeerOrder beerOrder = createBeerOrder();
    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).orElse(null);

      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
    });

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      BeerOrderLine line = foundOrder.getBeerOrderLines().iterator().next();
      assertEquals(line.getOrderQuantity(), line.getQuantityAllocated());
    });

    BeerOrder savedBeerOrder2 = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);

    assertNotNull(savedBeerOrder2);
    assertEquals(BeerOrderStatusEnum.ALLOCATED, savedBeerOrder2.getOrderStatus());
    savedBeerOrder2.getBeerOrderLines()
        .forEach(line -> assertEquals(line.getOrderQuantity(), line.getQuantityAllocated()));
  }

  @Test
  void testFailedValidation() {

    BeerOrder beerOrder = createBeerOrder();
    beerOrder.setCustomerRef("fail-validation");

    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);

      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatusEnum.VALIDATION_EXCEPTION, foundOrder.getOrderStatus());
    });
  }

  @Test
  void testNewToPickedUp() {
    BeerOrder beerOrder = createBeerOrder();

    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
    });

    beerOrderManager.beerOrderPickedUp(savedBeerOrder.getId());

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatusEnum.PICKED_UP, foundOrder.getOrderStatus());
    });

    BeerOrder pickedUpOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);

    assertNotNull(pickedUpOrder);
    assertEquals(BeerOrderStatusEnum.PICKED_UP, pickedUpOrder.getOrderStatus());
  }

  @Test
  void testAllocationFailure() {
    BeerOrder beerOrder = createBeerOrder();
    beerOrder.setCustomerRef("fail-allocation");

    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatusEnum.ALLOCATION_EXCEPTION, foundOrder.getOrderStatus());
    });

    AllocationFailureEvent allocationFailureEvent = (AllocationFailureEvent) jmsTemplate.receiveAndConvert(
        ALLOCATE_FAILURE_QUEUE);

    assertNotNull(allocationFailureEvent);
    assertEquals(savedBeerOrder.getId(), allocationFailureEvent.getOrderId());
  }

  @Test
  void testPartialAllocation() {
    BeerOrder beerOrder = createBeerOrder();
    beerOrder.setCustomerRef("partial-allocation");

    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatusEnum.PENDING_INVENTORY, foundOrder.getOrderStatus());
    });
  }

  @Test
  void testValidationPendingToCancel() {
    BeerOrder beerOrder = createBeerOrder();
    beerOrder.setCustomerRef("dont-validate");

    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatusEnum.VALIDATION_PENDING, foundOrder.getOrderStatus());
    });

    beerOrderManager.cancelOrder(savedBeerOrder.getId());

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatusEnum.CANCELLED, foundOrder.getOrderStatus());
    });
  }

  @Test
  void testAllocationPendingToCancel() {
    BeerOrder beerOrder = createBeerOrder();
    beerOrder.setCustomerRef("dont-allocate");

    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatusEnum.ALLOCATION_PENDING, foundOrder.getOrderStatus());
    });

    beerOrderManager.cancelOrder(savedBeerOrder.getId());

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatusEnum.CANCELLED, foundOrder.getOrderStatus());
    });
  }

  @Test
  void testAllocatedToCancel() {
    BeerOrder beerOrder = createBeerOrder();

    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
    });

    beerOrderManager.cancelOrder(savedBeerOrder.getId());

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatusEnum.CANCELLED, foundOrder.getOrderStatus());
    });

    DeAllocateOrderRequest deallocateOrderRequest = (DeAllocateOrderRequest) jmsTemplate.receiveAndConvert(
        DEALLOCATE_ORDER_QUEUE);

    assertNotNull(deallocateOrderRequest);
    assertEquals(savedBeerOrder.getId(), deallocateOrderRequest.getBeerOrderDTO().getId());
  }

  private BeerOrder createBeerOrder() {
    BeerOrder beerOrder = BeerOrder.builder().customer(testCustomer).build();

    Set<BeerOrderLine> lines = new HashSet<>();
    lines.add(BeerOrderLine.builder().beerId(beerId).upc("0631234200036").orderQuantity(1)
        .beerOrder(beerOrder).build());

    beerOrder.setBeerOrderLines(lines);

    return beerOrder;
  }

}
