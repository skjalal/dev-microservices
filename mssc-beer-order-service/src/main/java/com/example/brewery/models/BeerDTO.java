package com.example.brewery.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
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
@NoArgsConstructor
@AllArgsConstructor
public class BeerDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = 94424235235325232L;

  UUID id;

  Integer version;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = Shape.STRING)
  OffsetDateTime createdDate;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = Shape.STRING)
  OffsetDateTime lastModifiedDate;

  String beerName;
  String beerStyle;
  String upc;

  @JsonFormat(shape = Shape.STRING)
  BigDecimal price;

  @Builder.Default
  Integer quantityOnHand = 0;
}
