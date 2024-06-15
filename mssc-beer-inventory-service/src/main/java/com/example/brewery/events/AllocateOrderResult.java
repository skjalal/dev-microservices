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
public class AllocateOrderResult {

  private BeerOrderDTO beerOrderDTO;

  @Builder.Default
  private Boolean allocationError = Boolean.FALSE;

  @Builder.Default
  private Boolean pendingInventory = Boolean.FALSE;

}
