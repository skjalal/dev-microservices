package com.example.beerorderservice.services.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.beerorderservice.domain.Customer;
import com.example.beerorderservice.repositories.CustomerRepository;
import com.example.beerorderservice.services.CustomerService;
import com.example.beerorderservice.web.mappers.CustomerMapper;
import com.example.brewery.models.CustomerDTO;
import com.example.brewery.models.CustomerPagedList;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest(classes = {CustomerServiceImpl.class})
class CustomerServiceImplTest {

  @Autowired
  CustomerService customerService;

  @MockBean
  CustomerRepository customerRepository;

  @MockBean
  CustomerMapper customerMapper;

  Customer customer;
  CustomerDTO customerDTO;

  @BeforeEach
  void setUp() {
    Timestamp datetime = Timestamp.valueOf(LocalDateTime.now());
    customer = Customer.builder().id(UUID.randomUUID()).customerName("Test").version(1L)
        .apiKey(UUID.randomUUID()).beerOrders(Set.of()).createdDate(datetime)
        .lastModifiedDate(datetime).build();
    customerDTO = CustomerDTO.builder().id(UUID.randomUUID()).name("Test").version(1)
        .createdDate(OffsetDateTime.now()).lastModifiedDate(OffsetDateTime.now()).build();
  }

  @Test
  void listCustomers() {
    Pageable pageable = PageRequest.of(1, 10);
    when(customerRepository.findAll(any(Pageable.class))).thenReturn(
        new PageImpl<>(List.of(customer), pageable, 1));
    when(customerMapper.customerToDto(any(Customer.class))).thenReturn(customerDTO);
    CustomerPagedList list = customerService.listCustomers(pageable);
    assertNotNull(list);
  }
}