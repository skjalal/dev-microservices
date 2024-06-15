package com.example.beerorderservice.services;

import com.example.beerorderservice.exceptions.NotFoundException;
import com.example.brewery.models.BeerOrderDTO;
import com.example.brewery.models.BeerOrderPagedList;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface BeerOrderService {

  BeerOrderPagedList listOrders(UUID customerId, Pageable pageable);

  BeerOrderDTO placeOrder(UUID customerId, BeerOrderDTO beerOrderDto);

  BeerOrderDTO getOrderById(UUID customerId, UUID orderId);

  void pickupOrder(UUID customerId, UUID orderId);

  default NotFoundException notFound() {
    return new NotFoundException("Beer order not found");
  }

  default NotFoundException notFound(String message) {
    return new NotFoundException(message);
  }
}
