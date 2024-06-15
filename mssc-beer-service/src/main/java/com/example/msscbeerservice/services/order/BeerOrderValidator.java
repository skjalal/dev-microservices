package com.example.msscbeerservice.services.order;

import com.example.brewery.events.BeerOrderDTO;
import com.example.msscbeerservice.domain.Beer;
import com.example.msscbeerservice.repository.BeerRepository;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BeerOrderValidator {

  private final BeerRepository beerRepository;

  public Boolean validateOrder(BeerOrderDTO dto) {
    AtomicInteger beerNotFound = new AtomicInteger(0);
    dto.getBeerOrderLines().forEach(o -> beerRepository.findByUpc(o.getUpc())
        .ifPresentOrElse(this::orderFound, beerNotFound::incrementAndGet));
    return beerNotFound.get() == 0;
  }

  private void orderFound(Beer beer) {
    log.debug("Beer found by UPC: {}", beer.getUpc());
  }
}
