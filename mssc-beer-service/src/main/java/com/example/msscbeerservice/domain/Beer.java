package com.example.msscbeerservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Beer {

  @Id
  @UuidGenerator
  @GeneratedValue(generator = "UUID")
  @Column(length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
  UUID id;

  @Version
  Long version;

  @CreationTimestamp
  @Column(updatable = false)
  Timestamp createdDate;

  @UpdateTimestamp
  Timestamp lastModifiedDate;
  String beerName;
  String beerStyle;

  @Column(unique = true)
  String upc;
  BigDecimal price;
  Integer minOnHand;
  Integer quantityToBrew;
}
