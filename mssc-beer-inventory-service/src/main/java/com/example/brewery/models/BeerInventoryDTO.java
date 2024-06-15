package com.example.brewery.models;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeerInventoryDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = 4233213132L;

  UUID id;
  OffsetDateTime createdDate;
  OffsetDateTime lastModifiedDate;
  UUID beerId;
  Integer quantityOnHand;
}
