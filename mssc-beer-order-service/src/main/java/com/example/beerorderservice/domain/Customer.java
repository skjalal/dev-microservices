package com.example.beerorderservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Customer extends BaseEntity {

  @Builder
  public Customer(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
      String customerName, UUID apiKey, Set<BeerOrder> beerOrders) {
    super(id, version, createdDate, lastModifiedDate);
    this.customerName = customerName;
    this.apiKey = apiKey;
    this.beerOrders = beerOrders;
  }

  String customerName;

  @Column(length = 36, columnDefinition = "varchar")
  UUID apiKey;

  @OneToMany(mappedBy = "customer")
  Set<BeerOrder> beerOrders;
}
