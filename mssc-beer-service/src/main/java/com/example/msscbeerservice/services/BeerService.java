package com.example.msscbeerservice.services;

import com.example.msscbeerservice.exceptions.NotFoundException;
import com.example.brewery.models.BeerDTO;
import com.example.brewery.models.BeerPagedList;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;

public interface BeerService {

  BeerDTO getById(UUID id, boolean showInventoryOnHand);

  BeerDTO saveNewBeer(BeerDTO beerDTO);

  BeerDTO updateBeer(UUID id, BeerDTO beerDTO);

  default NotFoundException toDefaultThrow() {
    return new NotFoundException("Beer Not Found");
  }

  BeerPagedList listBeers(String beerName, String beerStyle, PageRequest pageRequest,
      boolean showInventoryOnHand);

  BeerDTO getByUpc(String upc);
}
