package com.example.beerorderservice.services.impl;

import com.example.beerorderservice.domain.BeerOrder;
import com.example.beerorderservice.domain.BeerOrderStatusEnum;
import com.example.beerorderservice.domain.Customer;
import com.example.beerorderservice.repositories.BeerOrderRepository;
import com.example.beerorderservice.repositories.CustomerRepository;
import com.example.beerorderservice.services.BeerOrderManager;
import com.example.beerorderservice.services.BeerOrderService;
import com.example.beerorderservice.web.mappers.BeerOrderMapper;
import com.example.brewery.models.BeerOrderDTO;
import com.example.brewery.models.BeerOrderPagedList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BeerOrderServiceImpl implements BeerOrderService {

  private final BeerOrderRepository beerOrderRepository;
  private final CustomerRepository customerRepository;
  private final BeerOrderMapper beerOrderMapper;
  private final BeerOrderManager beerOrderManager;

  @Override
  public BeerOrderPagedList listOrders(UUID customerId, Pageable pageable) {
    Page<BeerOrder> beerOrderPage = customerRepository.findById(customerId)
        .map(customer -> beerOrderRepository.findAllByCustomer(customer, pageable))
        .orElseGet(Page::empty);
    List<BeerOrderDTO> contents = beerOrderPage.map(beerOrderMapper::beerOrderToDto).getContent();
    Pageable pageDetails = PageRequest.of(beerOrderPage.getPageable().getPageNumber(),
        beerOrderPage.getPageable().getPageSize());
    long count = beerOrderPage.getTotalElements();
    return new BeerOrderPagedList(contents, pageDetails, count);
  }

  @Override
  public BeerOrderDTO placeOrder(UUID customerId, BeerOrderDTO beerOrderDto) {
    Customer customer = fetchCustomer(customerId);
    BeerOrder beerOrder = beerOrderMapper.dtoToBeerOrder(beerOrderDto);
    beerOrder.setCustomer(customer);
    beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);
    beerOrder.getBeerOrderLines().forEach(line -> line.setBeerOrder(beerOrder));
    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
    log.debug("Saved Beer Order: {}", savedBeerOrder.getId());
    return beerOrderMapper.beerOrderToDto(savedBeerOrder);
  }

  @Override
  public BeerOrderDTO getOrderById(UUID customerId, UUID orderId) {
    Customer customer = fetchCustomer(customerId);
    return beerOrderRepository.findByIdAndCustomer(orderId, customer)
        .map(beerOrderMapper::beerOrderToDto).orElseThrow(this::notFound);
  }

  @Override
  public void pickupOrder(UUID customerId, UUID orderId) {
    beerOrderManager.beerOrderPickedUp(orderId);
  }

  private Customer fetchCustomer(UUID customerId) {
    return customerRepository.findById(customerId)
        .orElseThrow(() -> notFound("Customer Not Found"));
  }
}
