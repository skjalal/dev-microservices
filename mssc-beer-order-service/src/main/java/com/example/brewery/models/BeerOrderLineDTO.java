package com.example.brewery.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
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
public class BeerOrderLineDTO {

  UUID id;

  Integer version;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
  OffsetDateTime createdDate;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
  OffsetDateTime lastModifiedDate;

  String upc;
  String beerName;
  String beerStyle;
  UUID beerId;
  Integer orderQuantity = 0;
  BigDecimal price;
  Integer quantityAllocated;
}
