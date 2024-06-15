package com.example.beerorderservice.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class BeerOrder extends BaseEntity {

  @Builder
  public BeerOrder(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
      String customerRef, Customer customer, Set<BeerOrderLine> beerOrderLines,
      BeerOrderStatusEnum orderStatus, String orderStatusCallbackUrl) {
    super(id, version, createdDate, lastModifiedDate);
    this.customerRef = customerRef;
    this.customer = customer;
    this.beerOrderLines = beerOrderLines;
    this.orderStatus = orderStatus;
    this.orderStatusCallbackUrl = orderStatusCallbackUrl;
  }

  String customerRef;

  @ManyToOne
  Customer customer;

  @Fetch(value = FetchMode.JOIN)
  @OneToMany(mappedBy = "beerOrder", cascade = CascadeType.ALL)
  Set<BeerOrderLine> beerOrderLines;

  BeerOrderStatusEnum orderStatus = BeerOrderStatusEnum.NEW;
  String orderStatusCallbackUrl;
}
