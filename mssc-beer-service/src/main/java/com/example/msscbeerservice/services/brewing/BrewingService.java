package com.example.msscbeerservice.services.brewing;

import com.example.msscbeerservice.config.JmsConfig;
import com.example.msscbeerservice.domain.Beer;
import com.example.brewery.events.BrewBeerEvent;
import com.example.msscbeerservice.repository.BeerRepository;
import com.example.msscbeerservice.services.inventory.BeerInventoryService;
import com.example.msscbeerservice.web.mappers.BeerMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BrewingService {

  private final BeerRepository beerRepository;
  private final BeerInventoryService beerInventoryService;
  private final JmsTemplate jmsTemplate;
  private final BeerMapper beerMapper;

  @Scheduled(fixedRate = 5000L)
  public void checkForLowInventory() {
    List<Beer> beers = beerRepository.findAll();
    beers.forEach(this::processBeer);
  }

  private void processBeer(Beer beer) {
    Integer invQOH = beerInventoryService.getOnHandInventory(beer.getId());
    log.debug("Checking Inventory for: {} / {}", beer.getBeerName(), beer.getId());
    log.debug("Min OnHand is: {}", beer.getMinOnHand());
    log.debug("Inventory is: {}", invQOH);

    if (beer.getMinOnHand() >= invQOH) {
      jmsTemplate.convertAndSend(JmsConfig.BREWING_REQUEST_QUEUE,
          new BrewBeerEvent(beerMapper.beerToBeerDTO(beer)));
    }
  }
}
