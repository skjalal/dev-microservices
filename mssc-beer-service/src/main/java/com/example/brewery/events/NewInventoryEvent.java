package com.example.brewery.events;

import com.example.brewery.models.BeerDTO;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NewInventoryEvent extends BeerEvent {

  public NewInventoryEvent(BeerDTO beerDTO) {
    super(beerDTO);
  }
}
