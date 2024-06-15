package com.example.brewery.events;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeerEvent implements Serializable {

  @Serial
  private static final long serialVersionUID = -5781515597148163111L;

  BeerDTO beerDTO;
}
