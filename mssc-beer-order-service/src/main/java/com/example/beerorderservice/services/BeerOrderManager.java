package com.example.beerorderservice.services;

import com.example.beerorderservice.domain.BeerOrder;
import com.example.brewery.models.BeerOrderDTO;
import java.util.UUID;

public interface BeerOrderManager {

  String ORDER_ID_HEADER = "ORDER_ID_HEADER";

  BeerOrder newBeerOrder(BeerOrder beerOrder);

  void processValidationResult(UUID orderId, Boolean isValid);

  void beerOrderAllocationPassed(BeerOrderDTO beerOrder);

  void beerOrderAllocationPendingInventory(BeerOrderDTO beerOrder);

  void beerOrderAllocationFailed(BeerOrderDTO beerOrder);

  void beerOrderPickedUp(UUID id);

  void cancelOrder(UUID id);
}
