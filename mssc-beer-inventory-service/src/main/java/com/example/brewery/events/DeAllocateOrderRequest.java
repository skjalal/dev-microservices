package com.example.brewery.events;

import com.example.brewery.models.BeerOrderDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeAllocateOrderRequest {

  private BeerOrderDTO beerOrderDTO;
}
