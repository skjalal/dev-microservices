package com.example.brewery.events;

import com.example.brewery.models.BeerDTO;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeerEvent implements Serializable {

  @Serial
  private static final long serialVersionUID = 134343132314141L;
  private BeerDTO beerDTO;

}
