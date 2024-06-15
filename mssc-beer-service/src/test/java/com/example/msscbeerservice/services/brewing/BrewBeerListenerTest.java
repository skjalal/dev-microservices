package com.example.msscbeerservice.services.brewing;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.example.msscbeerservice.domain.Beer;
import com.example.brewery.events.BrewBeerEvent;
import com.example.brewery.events.NewInventoryEvent;
import com.example.msscbeerservice.repository.BeerRepository;
import com.example.brewery.models.BeerDTO;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;

@SpringBootTest(classes = {BrewBeerListener.class})
class BrewBeerListenerTest {

  @Autowired
  BrewBeerListener brewBeerListener;

  @MockBean
  BeerRepository beerRepository;

  @MockBean
  JmsTemplate jmsTemplate;

  Beer beer;
  BeerDTO beerDTO;

  @BeforeEach
  void setUp() {
    beerDTO = BeerDTO.builder().id(UUID.randomUUID()).build();
    beer = Beer.builder().id(UUID.randomUUID()).quantityToBrew(2).minOnHand(2).build();
  }

  @ParameterizedTest
  @ValueSource(strings = {"success", "failure"})
  void listen(String input) {
    if (input.equalsIgnoreCase("failure")) {
      when(beerRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
    } else {
      when(beerRepository.findById(any(UUID.class))).thenReturn(Optional.of(beer));
      doNothing().when(jmsTemplate).convertAndSend(anyString(), any(NewInventoryEvent.class));
    }
    assertDoesNotThrow(() -> brewBeerListener.listen(new BrewBeerEvent(beerDTO)));
  }
}