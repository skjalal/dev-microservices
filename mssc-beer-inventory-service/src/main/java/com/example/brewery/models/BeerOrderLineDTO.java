package com.example.brewery.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeerOrderLineDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = 42332131322312L;

  @JsonProperty("id")
  UUID id = null;

  @JsonProperty("version")
  Integer version = null;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
  @JsonProperty("createdDate")
  OffsetDateTime createdDate = null;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
  @JsonProperty("lastModifiedDate")
  OffsetDateTime lastModifiedDate = null;

  String upc;
  String beerName;
  String beerStyle;
  UUID beerId;
  Integer orderQuantity = 0;
  Integer quantityAllocated;
  BigDecimal price;
}
