package com.example.brewery.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
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
public class BeerDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = -5815566940065181210L;

  @Null
  UUID id;

  @Null
  Integer version;

  @Null
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
  OffsetDateTime createdDate;

  @Null
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
  OffsetDateTime lastModifiedDate;

  @NotBlank
  String beerName;

  @NotNull
  String beerStyle;

  @NotNull
  String upc;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Positive
  @NotNull
  BigDecimal price;

  Integer quantityOnHand;
}
