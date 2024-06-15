package com.example.beerinventoryservice.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.beerinventoryservice.domain.BeerInventory;
import com.example.beerinventoryservice.repositories.BeerInventoryRepository;
import com.example.beerinventoryservice.services.listeners.NewInventoryListener;
import com.example.brewery.events.BeerDTO;
import com.example.brewery.events.NewInventoryEvent;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {NewInventoryListener.class})
class NewInventoryListenerTest {

  @Autowired
  NewInventoryListener newInventoryListener;

  @MockBean
  BeerInventoryRepository beerInventoryRepository;

  @Test
  void listen() {
    when(beerInventoryRepository.save(any(BeerInventory.class))).thenReturn(new BeerInventory());
    NewInventoryEvent event = new NewInventoryEvent(
        BeerDTO.builder().id(UUID.randomUUID()).upc("0003121").quantityOnHand(2).build());
    assertDoesNotThrow(() -> newInventoryListener.listen(event));
  }
}