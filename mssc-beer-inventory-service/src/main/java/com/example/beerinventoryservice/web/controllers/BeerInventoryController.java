package com.example.beerinventoryservice.web.controllers;

import com.example.beerinventoryservice.repositories.BeerInventoryRepository;
import com.example.beerinventoryservice.web.mappers.BeerInventoryMapper;
import com.example.brewery.models.BeerInventoryDTO;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BeerInventoryController {

  private final BeerInventoryRepository beerInventoryRepository;
  private final BeerInventoryMapper beerInventoryMapper;

  @GetMapping("/api/v1/beer/{beerId}/inventory")
  public List<BeerInventoryDTO> listBeersById(@PathVariable(name = "beerId") UUID beerId) {
    log.debug("Finding Inventory for beerId:" + beerId);

    return beerInventoryRepository.findAllByBeerId(beerId).stream()
        .map(beerInventoryMapper::beerInventoryToBeerInventoryDto).toList();
  }
}
