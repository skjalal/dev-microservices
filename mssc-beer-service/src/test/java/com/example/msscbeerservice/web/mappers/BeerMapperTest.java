package com.example.msscbeerservice.web.mappers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.msscbeerservice.domain.Beer;
import com.example.msscbeerservice.services.inventory.BeerInventoryService;
import com.example.brewery.models.BeerDTO;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {BeerMapperImpl.class, BeerMapperImpl_.class})
class BeerMapperTest {

  @Autowired
  BeerMapper beerMapper;

  @MockBean
  BeerInventoryService beerInventoryService;

  Beer beer;
  BeerDTO beerDTO;

  @BeforeEach
  void setUp() {
    beer = Beer.builder().id(UUID.randomUUID()).createdDate(Timestamp.valueOf(LocalDateTime.now()))
        .build();
    beerDTO = BeerDTO.builder().createdDate(OffsetDateTime.now()).build();
  }

  @Test
  void beerToBeerDTO() {
    assertNotNull(beerMapper.beerToBeerDTO(beer));
  }

  @Test
  void beerDTOToBeer() {
    assertNotNull(beerMapper.beerDTOToBeer(beerDTO));
  }

  @Test
  void beerToBeerDTOWithInventory() {
    when(beerInventoryService.getOnHandInventory(any(UUID.class))).thenReturn(2);
    assertNotNull(beerMapper.beerToBeerDTOWithInventory(beer));
  }
}