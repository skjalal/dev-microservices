package com.example.brewery.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeerOrderDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = 42332131222L;

  @JsonProperty("id")
  UUID id;

  @JsonProperty("version")
  Integer version;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
  @JsonProperty("createdDate")
  OffsetDateTime createdDate;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
  @JsonProperty("lastModifiedDate")
  OffsetDateTime lastModifiedDate;

  UUID customerId;
  String customerRef;
  private List<BeerOrderLineDTO> beerOrderLines;
  String orderStatus;
  String orderStatusCallbackUrl;

}
