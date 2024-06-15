package com.example.msscbeerservice.services.brewing;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.example.msscbeerservice.domain.Beer;
import com.example.brewery.events.BrewBeerEvent;
import com.example.msscbeerservice.repository.BeerRepository;
import com.example.msscbeerservice.services.inventory.BeerInventoryService;
import com.example.msscbeerservice.web.mappers.BeerMapper;
import com.example.brewery.models.BeerDTO;
import com.example.brewery.models.BeerStyleEnum;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;

@SpringBootTest(classes = {BrewingService.class})
class BrewingServiceTest {

  @Autowired
  BrewingService brewingService;

  @MockBean
  BeerRepository beerRepository;

  @MockBean
  BeerInventoryService beerInventoryService;

  @MockBean
  JmsTemplate jmsTemplate;

  @MockBean
  BeerMapper beerMapper;

  Beer beer;
  BeerDTO beerDTO;

  @BeforeEach
  void setUp() {
    beerDTO = BeerDTO.builder().id(UUID.randomUUID()).beerName("Test").version(1)
        .price(BigDecimal.valueOf(231.009)).quantityOnHand(2).upc("0000323131")
        .beerStyle(BeerStyleEnum.ALE).createdDate(OffsetDateTime.now())
        .lastModifiedDate(OffsetDateTime.now()).build();
    beer = Beer.builder().id(UUID.randomUUID()).beerName("Test").minOnHand(2).build();
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3})
  void checkForLowInventory(int invOnHand) {
    when(beerRepository.findAll()).thenReturn(List.of(beer));
    when(beerInventoryService.getOnHandInventory(any(UUID.class))).thenReturn(invOnHand);
    if (invOnHand < 3) {
      when(beerMapper.beerToBeerDTO(any(Beer.class))).thenReturn(beerDTO);
      doNothing().when(jmsTemplate).convertAndSend(anyString(), any(BrewBeerEvent.class));
    }
    assertDoesNotThrow(brewingService::checkForLowInventory);
  }
}