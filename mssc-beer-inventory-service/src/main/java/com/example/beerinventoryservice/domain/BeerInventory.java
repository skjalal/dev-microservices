package com.example.beerinventoryservice.domain;

import jakarta.persistence.Entity;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class BeerInventory extends BaseEntity {

  @Builder
  public BeerInventory(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
      UUID beerId,
      String upc, Integer quantityOnHand) {
    super(id, version, createdDate, lastModifiedDate);
    this.beerId = beerId;
    this.upc = upc;
    this.quantityOnHand = quantityOnHand;
  }

  UUID beerId;
  String upc;
  Integer quantityOnHand = 0;
}
