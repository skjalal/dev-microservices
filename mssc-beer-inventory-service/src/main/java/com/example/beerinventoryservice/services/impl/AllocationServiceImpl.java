package com.example.beerinventoryservice.services.impl;

import com.example.beerinventoryservice.domain.BeerInventory;
import com.example.beerinventoryservice.repositories.BeerInventoryRepository;
import com.example.beerinventoryservice.services.AllocationService;
import com.example.brewery.models.BeerOrderDTO;
import com.example.brewery.models.BeerOrderLineDTO;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AllocationServiceImpl implements AllocationService {

  private final BeerInventoryRepository beerInventoryRepository;

  @Override
  public Boolean allocateOrder(BeerOrderDTO beerOrderDto) {
    log.debug("Allocating OrderId: {}", beerOrderDto.getId());

    AtomicInteger totalOrdered = new AtomicInteger();
    AtomicInteger totalAllocated = new AtomicInteger();

    beerOrderDto.getBeerOrderLines().forEach(beerOrderLine -> {
      Integer orderQuantity = Optional.ofNullable(beerOrderLine.getOrderQuantity()).orElse(0);
      Integer quantityAllocated = Optional.ofNullable(beerOrderLine.getQuantityAllocated())
          .orElse(0);
      if (orderQuantity - quantityAllocated > 0) {
        allocateBeerOrderLine(beerOrderLine);
      }
      totalOrdered.set(totalOrdered.get() + orderQuantity);
      Integer updatedQuantityAllocated = Optional.ofNullable(beerOrderLine.getQuantityAllocated())
          .orElse(0);
      totalAllocated.set(totalAllocated.get() + updatedQuantityAllocated);
    });
    log.debug("Total Ordered: {} Total Allocated: {}", totalOrdered.get(), totalAllocated.get());
    return totalOrdered.get() == totalAllocated.get();
  }

  private void allocateBeerOrderLine(BeerOrderLineDTO beerOrderLine) {
    List<BeerInventory> beerInventoryList = beerInventoryRepository.findAllByUpc(
        beerOrderLine.getUpc());

    beerInventoryList.forEach(beerInventory -> processBeerInventory(beerInventory, beerOrderLine));
  }

  private void processBeerInventory(BeerInventory beerInventory, BeerOrderLineDTO beerOrderLine) {
    int inventory = Optional.ofNullable(beerInventory.getQuantityOnHand()).orElse(0);
    int orderQty = Optional.ofNullable(beerOrderLine.getOrderQuantity()).orElse(0);
    int allocatedQty = Optional.ofNullable(beerOrderLine.getQuantityAllocated()).orElse(0);
    int qtyToAllocate = orderQty - allocatedQty;

    if (inventory >= qtyToAllocate) { // full allocation
      inventory = inventory - qtyToAllocate;
      beerOrderLine.setQuantityAllocated(orderQty);
      beerInventory.setQuantityOnHand(inventory);

      beerInventoryRepository.save(beerInventory);
    } else if (inventory > 0) { //partial allocation
      beerOrderLine.setQuantityAllocated(allocatedQty + inventory);
      beerInventory.setQuantityOnHand(0);
    }

    int updatedInventory = Optional.ofNullable(beerInventory.getQuantityOnHand()).orElse(0);
    if (updatedInventory == 0) {
      beerInventoryRepository.delete(beerInventory);
    }
  }

  @Override
  public void deallocateOrder(BeerOrderDTO beerOrderDto) {
    beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
      BeerInventory beerInventory = BeerInventory.builder().beerId(beerOrderLineDto.getBeerId())
          .upc(beerOrderLineDto.getUpc()).quantityOnHand(beerOrderLineDto.getQuantityAllocated())
          .build();

      BeerInventory savedInventory = beerInventoryRepository.save(beerInventory);

      log.debug("Saved Inventory for beer upc: {} inventory id: {}", savedInventory.getUpc(),
          savedInventory.getId());
    });
  }
}
