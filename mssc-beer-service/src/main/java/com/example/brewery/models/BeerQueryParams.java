package com.example.brewery.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BeerQueryParams {

  Integer pageNumber = 0;
  Integer pageSize = 25;
  String beerName;
  BeerStyleEnum beerStyle;
  Boolean showInventoryOnHand = Boolean.FALSE;
}
