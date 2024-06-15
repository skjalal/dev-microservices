package com.example.beerinventoryservice.bootstrap;

import com.example.beerinventoryservice.domain.BeerInventory;
import com.example.beerinventoryservice.repositories.BeerInventoryRepository;
import com.example.beerinventoryservice.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BeerInventoryBootstrap implements CommandLineRunner {

  private final BeerInventoryRepository beerInventoryRepository;

  @Override
  public void run(String... args) {
    if (beerInventoryRepository.count() == 0) {
      loadInitialInv();
    }
  }

  private void loadInitialInv() {
    beerInventoryRepository.save(
        BeerInventory.builder().beerId(Constants.BEER_1_UUID).upc(Constants.BEER_1_UPC).quantityOnHand(50).build());

    beerInventoryRepository.save(
        BeerInventory.builder().beerId(Constants.BEER_2_UUID).upc(Constants.BEER_2_UPC).quantityOnHand(50).build());

    beerInventoryRepository.saveAndFlush(
        BeerInventory.builder().beerId(Constants.BEER_3_UUID).upc(Constants.BEER_3_UPC).quantityOnHand(50).build());

    log.debug("Loaded Inventory. Record count: {}", beerInventoryRepository.count());
  }
}
