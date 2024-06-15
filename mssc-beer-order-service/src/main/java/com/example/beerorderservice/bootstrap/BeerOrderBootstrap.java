package com.example.beerorderservice.bootstrap;

import com.example.beerorderservice.domain.Customer;
import com.example.beerorderservice.repositories.CustomerRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BeerOrderBootstrap implements CommandLineRunner {

  private final CustomerRepository customerRepository;

  @Override
  public void run(String... args) {
    if (customerRepository.count() == 0) {
      Customer customer = customerRepository.save(
          Customer.builder().customerName("Tasting Room").apiKey(UUID.randomUUID()).build());
      log.info("Customer {} created", customer.getId());
    }
  }
}
