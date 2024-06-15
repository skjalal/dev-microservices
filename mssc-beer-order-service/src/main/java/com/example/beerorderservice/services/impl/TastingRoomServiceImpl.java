package com.example.beerorderservice.services.impl;

import com.example.beerorderservice.domain.Customer;
import com.example.beerorderservice.repositories.CustomerRepository;
import com.example.beerorderservice.services.BeerOrderService;
import com.example.beerorderservice.services.TastingRoomService;
import com.example.brewery.models.BeerOrderDTO;
import com.example.brewery.models.BeerOrderLineDTO;
import jakarta.annotation.PostConstruct;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TastingRoomServiceImpl implements TastingRoomService {

  private final CustomerRepository customerRepository;
  private final BeerOrderService beerOrderService;
  private final List<String> beerUpcs = new ArrayList<>(3);

  @PostConstruct
  public void init() {
    beerUpcs.add("0631234200036");
  }

  @Override
  @Scheduled(fixedRate = 2000)
  public void placeTastingRoomOrder() {
    List<Customer> customerList = customerRepository.findAllByCustomerNameLike("Tasting Room");
    if (customerList.size() == 1) { //should be just one
      doPlaceOrder(customerList.get(0));
    } else {
      log.error("Too many or too few tasting room customers found");
    }
  }

  private void doPlaceOrder(Customer customer) {
    String beerToOrder = beerUpcs.get(new SecureRandom().nextInt(beerUpcs.size()));
    BeerOrderLineDTO beerOrderLine = BeerOrderLineDTO.builder().upc(beerToOrder)
        .orderQuantity(new SecureRandom().nextInt(6)).build();
    List<BeerOrderLineDTO> beerOrderLineSet = new ArrayList<>();
    beerOrderLineSet.add(beerOrderLine);
    BeerOrderDTO beerOrder = BeerOrderDTO.builder().customerId(customer.getId())
        .customerRef(UUID.randomUUID().toString()).beerOrderLines(beerOrderLineSet).build();

    BeerOrderDTO savedOrder = beerOrderService.placeOrder(customer.getId(), beerOrder);
    log.debug("Beer Ordered: {}", savedOrder);
  }
}
