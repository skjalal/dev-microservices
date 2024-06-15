package com.example.beerorderservice.services.beer;

import com.example.brewery.models.BeerDTO;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {

  Optional<BeerDTO> getBeerById(UUID uuid);

  Optional<BeerDTO> getBeerByUpc(String upc);
}
