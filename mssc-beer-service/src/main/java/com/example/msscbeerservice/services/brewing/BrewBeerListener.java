package com.example.msscbeerservice.services.brewing;

import com.example.msscbeerservice.config.JmsConfig;
import com.example.msscbeerservice.domain.Beer;
import com.example.brewery.events.BrewBeerEvent;
import com.example.brewery.events.NewInventoryEvent;
import com.example.msscbeerservice.exceptions.NotFoundException;
import com.example.msscbeerservice.repository.BeerRepository;
import com.example.brewery.models.BeerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BrewBeerListener {

  private final BeerRepository beerRepository;
  private final JmsTemplate jmsTemplate;

  @JmsListener(destination = JmsConfig.BREWING_REQUEST_QUEUE)
  public void listen(BrewBeerEvent event) {
    try {
      BeerDTO beerDTO = event.getBeerDTO();
      Beer beer = beerRepository.findById(beerDTO.getId())
          .orElseThrow(() -> new NotFoundException("Beer not found of ID: " + beerDTO.getId()));
      beerDTO.setQuantityOnHand(beer.getQuantityToBrew());
      NewInventoryEvent newInventoryEvent = new NewInventoryEvent(beerDTO);

      log.debug("Brewed beer {} : QOH: {}", beer.getMinOnHand(), beerDTO.getQuantityOnHand());

      jmsTemplate.convertAndSend(JmsConfig.NEW_INVENTORY_QUEUE, newInventoryEvent);
    } catch (Exception e) {
      log.error("Failed to process JMS record", e);
    }
  }

}
