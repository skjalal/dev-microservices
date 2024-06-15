package com.example.brewery.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeerOrderDTO {

  UUID id;

  Integer version;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
  OffsetDateTime createdDate;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
  OffsetDateTime lastModifiedDate;

  UUID customerId;
  String customerRef;
  List<BeerOrderLineDTO> beerOrderLines;
  String orderStatus;
  String orderStatusCallbackUrl;
}
