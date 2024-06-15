package com.example.brewery.events;

import com.example.brewery.models.BeerDTO;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BrewBeerEvent extends BeerEvent {

  public BrewBeerEvent(BeerDTO beerDTO) {
    super(beerDTO);
  }
}
