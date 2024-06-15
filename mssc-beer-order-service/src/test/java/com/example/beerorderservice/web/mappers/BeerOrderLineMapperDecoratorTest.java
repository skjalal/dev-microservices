package com.example.beerorderservice.web.mappers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.beerorderservice.domain.BeerOrderLine;
import com.example.beerorderservice.services.beer.BeerService;
import com.example.brewery.models.BeerDTO;
import com.example.brewery.models.BeerOrderLineDTO;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {BeerOrderLineMapperImpl.class, BeerOrderLineMapperImpl_.class})
class BeerOrderLineMapperDecoratorTest {

  @Autowired
  BeerOrderLineMapper beerOrderLineMapper;

  @MockBean
  BeerService beerService;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testBeerOrderLineToDTO() {
    BeerOrderLine orderLine = BeerOrderLine.builder().upc("000233131321")
        .createdDate(Timestamp.valueOf(LocalDateTime.now())).build();
    BeerDTO beerDTO = BeerDTO.builder().beerName("T").beerStyle("AL")
        .price(BigDecimal.valueOf(23.54)).id(UUID.randomUUID()).build();
    when(beerService.getBeerByUpc(anyString())).thenReturn(Optional.of(beerDTO));
    BeerOrderLineDTO dto = beerOrderLineMapper.beerOrderLineToDto(orderLine);
    assertNotNull(dto);
  }

  @Test
  void testBeerOrderLineDTOtoEntity() {
    BeerOrderLineDTO dto =
        BeerOrderLineDTO.builder().beerId(UUID.randomUUID()).createdDate(OffsetDateTime.now()).build();
    BeerOrderLine beerOrderLine = beerOrderLineMapper.dtoToBeerOrderLine(dto);
    assertNotNull(beerOrderLine);
  }
}