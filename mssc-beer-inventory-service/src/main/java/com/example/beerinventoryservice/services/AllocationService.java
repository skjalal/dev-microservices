package com.example.beerinventoryservice.services;

import com.example.brewery.models.BeerOrderDTO;

public interface AllocationService {

  Boolean allocateOrder(BeerOrderDTO dto);

  void deallocateOrder(BeerOrderDTO dto);
}
