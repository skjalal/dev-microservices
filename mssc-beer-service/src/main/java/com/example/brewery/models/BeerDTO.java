package com.example.brewery.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
  private static final long serialVersionUID = 14424235235325232L;

  @Null
  UUID id;

  @Null
  Integer version;

  @Null
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = Shape.STRING)
  OffsetDateTime createdDate;

  @Null
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = Shape.STRING)
  OffsetDateTime lastModifiedDate;

  @NotBlank
  @Size(min = 3, max = 100)
  String beerName;

  @NotNull
  BeerStyleEnum beerStyle;

  @NotNull
  @Positive
  String upc;

  @NotNull
  @Positive
  @JsonFormat(shape = Shape.STRING)
  BigDecimal price;

  @Positive
  @Builder.Default
  Integer quantityOnHand = 0;
}
