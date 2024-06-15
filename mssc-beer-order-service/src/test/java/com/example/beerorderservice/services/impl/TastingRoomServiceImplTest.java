package com.example.beerorderservice.services.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.beerorderservice.domain.Customer;
import com.example.beerorderservice.repositories.CustomerRepository;
import com.example.beerorderservice.services.BeerOrderService;
import com.example.beerorderservice.services.TastingRoomService;
import com.example.brewery.models.BeerOrderDTO;
import com.example.brewery.models.BeerOrderLineDTO;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {TastingRoomServiceImpl.class})
class TastingRoomServiceImplTest {

  @Autowired
  TastingRoomService tastingRoomService;

  @MockBean
  CustomerRepository customerRepository;

  @MockBean
  BeerOrderService beerOrderService;

  Customer customer;
  BeerOrderDTO beerOrderDTO;

  @BeforeEach
  void setUp() {
    UUID id = UUID.randomUUID();
    OffsetDateTime dateTime = OffsetDateTime.now();
    BeerOrderLineDTO orderLineDTO = BeerOrderLineDTO.builder().orderQuantity(2).upc("00088272")
        .beerId(id).beerName("ALA").beerStyle("ALA").price(BigDecimal.valueOf(12.989)).id(id)
        .version(1).createdDate(dateTime).lastModifiedDate(dateTime).build();
    beerOrderDTO = BeerOrderDTO.builder().id(id).version(1).orderStatus("NEW")
        .customerId(id).customerRef(id.toString()).orderStatusCallbackUrl("/re-order")
        .beerOrderLines(List.of(orderLineDTO)).createdDate(dateTime).lastModifiedDate(dateTime)
        .build();
    Timestamp datetime = Timestamp.valueOf(LocalDateTime.now());
    customer = Customer.builder().id(UUID.randomUUID()).customerName("Test").version(1L)
        .apiKey(UUID.randomUUID()).beerOrders(Set.of()).createdDate(datetime)
        .lastModifiedDate(datetime).build();
  }

  @ParameterizedTest
  @ValueSource(strings = {"success", "failure"})
  void testPlaceTastingRoomOrder(String input) {
    List<Customer> list = input.equals("success") ? List.of(customer) : List.of();
    when(customerRepository.findAllByCustomerNameLike(anyString())).thenReturn(list);
    if (input.equals("success")) {
      when(beerOrderService.placeOrder(any(UUID.class), any(BeerOrderDTO.class))).thenReturn(
          beerOrderDTO);
    }
    assertDoesNotThrow(tastingRoomService::placeTastingRoomOrder);
  }
}