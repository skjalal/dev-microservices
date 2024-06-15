package com.example.msscbeerservice.services.inventory.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeerInventoryDTO {

  UUID id;
  OffsetDateTime createdDate;
  OffsetDateTime lastModifiedDate;
  UUID beerId;
  Integer quantityOnHand;
}
