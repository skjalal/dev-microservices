package com.example.beerinventoryservice.web.mappers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.beerinventoryservice.domain.BeerInventory;
import com.example.brewery.models.BeerInventoryDTO;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class BeerInventoryMapperTest {

  BeerInventoryMapper beerInventoryMapper;
  BeerInventory beerInventory;
  BeerInventoryDTO beerInventoryDTO;

  @BeforeEach
  void setUp() {
    beerInventoryMapper = Mappers.getMapper(BeerInventoryMapper.class);
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    UUID id = UUID.randomUUID();
    beerInventory = BeerInventory.builder().id(id).beerId(id).upc("0000232").version(1L)
        .quantityOnHand(2).createdDate(timestamp).lastModifiedDate(null).build();
    beerInventory.isNew();
    beerInventoryDTO = BeerInventoryDTO.builder().id(id).beerId(id).quantityOnHand(2)
        .createdDate(OffsetDateTime.now()).lastModifiedDate(null).build();
  }

  @Test
  void beerInventoryDtoToBeerInventory() {
    BeerInventory inventory = beerInventoryMapper.beerInventoryDtoToBeerInventory(beerInventoryDTO);
    assertNotNull(inventory);
  }

  @Test
  void beerInventoryToBeerInventoryDto() {
    BeerInventoryDTO dto = beerInventoryMapper.beerInventoryToBeerInventoryDto(beerInventory);
    assertNotNull(dto);
  }
}