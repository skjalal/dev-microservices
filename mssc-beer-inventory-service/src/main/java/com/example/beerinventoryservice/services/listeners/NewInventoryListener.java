package com.example.beerinventoryservice.services.listeners;

import com.example.beerinventoryservice.config.JmsConfig;
import com.example.beerinventoryservice.domain.BeerInventory;
import com.example.beerinventoryservice.repositories.BeerInventoryRepository;
import com.example.brewery.events.NewInventoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class NewInventoryListener {

  private final BeerInventoryRepository beerInventoryRepository;

  @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
  public void listen(NewInventoryEvent event) {

    log.debug("Got Inventory: {}", event);

    beerInventoryRepository.save(
        BeerInventory.builder().beerId(event.getBeerDTO().getId()).upc(event.getBeerDTO().getUpc())
            .quantityOnHand(event.getBeerDTO().getQuantityOnHand()).build());
  }
}
