package com.example.beerorderservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
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
public class BeerOrderLine extends BaseEntity {

  @Builder
  public BeerOrderLine(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
      BeerOrder beerOrder, UUID beerId, String upc, Integer orderQuantity, Integer quantityAllocated) {
    super(id, version, createdDate, lastModifiedDate);
    this.beerOrder = beerOrder;
    this.beerId = beerId;
    this.upc = upc;
    this.orderQuantity = orderQuantity;
    this.quantityAllocated = quantityAllocated;
  }

  @ManyToOne
  BeerOrder beerOrder;

  UUID beerId;
  String upc;
  Integer orderQuantity = 0;
  Integer quantityAllocated = 0;
}
