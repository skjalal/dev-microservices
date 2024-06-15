package com.example.beerorderservice.services.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.example.beerorderservice.domain.BeerOrder;
import com.example.beerorderservice.domain.BeerOrderLine;
import com.example.beerorderservice.domain.BeerOrderStatusEnum;
import com.example.beerorderservice.domain.Customer;
import com.example.beerorderservice.exceptions.NotFoundException;
import com.example.beerorderservice.repositories.BeerOrderRepository;
import com.example.beerorderservice.repositories.CustomerRepository;
import com.example.beerorderservice.services.BeerOrderManager;
import com.example.beerorderservice.services.BeerOrderService;
import com.example.beerorderservice.web.mappers.BeerOrderMapper;
import com.example.brewery.models.BeerOrderDTO;
import com.example.brewery.models.BeerOrderLineDTO;
import com.example.brewery.models.BeerOrderPagedList;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest(classes = {BeerOrderServiceImpl.class})
class BeerOrderServiceImplTest {

  @Autowired
  BeerOrderService beerOrderService;

  @MockBean
  BeerOrderRepository beerOrderRepository;

  @MockBean
  CustomerRepository customerRepository;

  @MockBean
  BeerOrderMapper beerOrderMapper;

  @MockBean
  BeerOrderManager beerOrderManager;

  Customer customer;
  BeerOrder beerOrder;
  BeerOrderDTO beerOrderDTO;
  UUID id;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
    OffsetDateTime dateTime = OffsetDateTime.now();
    customer = Customer.builder().id(id).beerOrders(Set.of()).version(1L).apiKey(id)
        .customerName("Test").createdDate(timestamp).lastModifiedDate(timestamp).build();
    BeerOrderLine orderLine = BeerOrderLine.builder().id(id).upc("0000231212").beerOrder(beerOrder)
        .beerId(id).orderQuantity(1).version(1L).quantityAllocated(2).createdDate(timestamp)
        .lastModifiedDate(timestamp).build();
    beerOrder = BeerOrder.builder().id(id).beerOrderLines(Set.of(orderLine)).customerRef("jsgdjagf")
        .customer(customer).orderStatus(BeerOrderStatusEnum.NEW).version(1L)
        .orderStatusCallbackUrl("/api").createdDate(timestamp).lastModifiedDate(timestamp).build();
    beerOrder.isNew();
    BeerOrderLineDTO orderLineDTO = BeerOrderLineDTO.builder().beerId(id).id(id).beerName("DD")
        .beerStyle("ALA").price(BigDecimal.valueOf(221.998)).version(1).upc("0000023e322")
        .orderQuantity(2).createdDate(dateTime).lastModifiedDate(dateTime).build();
    beerOrderDTO = BeerOrderDTO.builder().id(id).version(1)
        .orderStatus("NEW").customerId(id)
        .customerRef(id.toString()).orderStatusCallbackUrl("/re-order")
        .beerOrderLines(List.of(orderLineDTO)).createdDate(dateTime).lastModifiedDate(dateTime)
        .build();
  }

  @Test
  void listOrders() {
    Pageable pageable = PageRequest.of(1, 10);
    when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.of(customer));
    when(
        beerOrderRepository.findAllByCustomer(any(Customer.class), any(Pageable.class))).thenReturn(
        new PageImpl<>(List.of(beerOrder), pageable, 1));
    when(beerOrderMapper.beerOrderToDto(any(BeerOrder.class))).thenReturn(beerOrderDTO);
    BeerOrderPagedList list = beerOrderService.listOrders(id, pageable);
    assertNotNull(list);
  }

  @Test
  void placeOrder() {
    when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.of(customer));
    when(beerOrderMapper.dtoToBeerOrder(any(BeerOrderDTO.class))).thenReturn(beerOrder);
    when(beerOrderManager.newBeerOrder(any(BeerOrder.class))).thenReturn(beerOrder);
    when(beerOrderMapper.beerOrderToDto(any(BeerOrder.class))).thenReturn(beerOrderDTO);
    BeerOrderDTO dto = beerOrderService.placeOrder(id, beerOrderDTO);
    assertNotNull(dto);
  }

  @ParameterizedTest
  @ValueSource(strings = {"success", "customer-not-found", "beer-not-found"})
  void getOrderById(String input) {
    if (input.equalsIgnoreCase("success")) {
      when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.of(customer));
      when(
          beerOrderRepository.findByIdAndCustomer(any(UUID.class), any(Customer.class))).thenReturn(
          Optional.of(beerOrder));
      when(beerOrderMapper.beerOrderToDto(any(BeerOrder.class))).thenReturn(beerOrderDTO);
      BeerOrderDTO dto = beerOrderService.getOrderById(id, id);
      assertNotNull(dto);
    } else if (input.equalsIgnoreCase("customer-not-found")) {
      when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
      assertThrows(NotFoundException.class, () -> beerOrderService.getOrderById(id, id));
    } else {
      when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.of(customer));
      when(
          beerOrderRepository.findByIdAndCustomer(any(UUID.class), any(Customer.class))).thenReturn(
          Optional.empty());
      assertThrows(NotFoundException.class, () -> beerOrderService.getOrderById(id, id));
    }
  }

  @Test
  void pickupOrder() {
    doNothing().when(beerOrderManager).beerOrderPickedUp(any(UUID.class));
    assertDoesNotThrow(() -> beerOrderService.pickupOrder(id, id));
  }
}