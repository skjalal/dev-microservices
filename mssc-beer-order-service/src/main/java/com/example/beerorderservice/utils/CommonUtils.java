package com.example.beerorderservice.utils;

import com.example.beerorderservice.domain.BeerOrder;
import com.example.beerorderservice.domain.BeerOrderStatusEnum;
import com.example.beerorderservice.repositories.BeerOrderRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class CommonUtils {

  public static void sleep(long millis) {
    try {
      TimeUnit.MILLISECONDS.sleep(millis);
    } catch (InterruptedException e) {
      log.error("Failed to sleep current thread", e);
      Thread.currentThread().interrupt();
    }
  }

  public static void awaitForStatus(BeerOrderRepository beerOrderRepository, UUID beerOrderId,
      BeerOrderStatusEnum statusEnum) {

    AtomicBoolean found = new AtomicBoolean(false);
    AtomicInteger loopCount = new AtomicInteger(0);

    while (!found.get()) {
      if (loopCount.incrementAndGet() > 10) {
        found.set(true);
        log.debug("Loop Retries exceeded");
      }

      Optional<BeerOrder> order = beerOrderRepository.findById(beerOrderId);
      if (order.isPresent()) {
        BeerOrder beerOrder = order.get();
        if (beerOrder.getOrderStatus().equals(statusEnum)) {
          found.set(true);
          log.debug("Order Found");
        } else {
          log.debug("Order Status Not Equal. Expected: {} Found: {}", statusEnum.name(),
              beerOrder.getOrderStatus().name());
        }
      } else {
        log.error("Order Id: {} Not Found", beerOrderId);
      }

      if (!found.get()) {
        try {
          log.debug("Sleeping for retry");
          TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
          log.error("Failed to wait the current thread", e);
          Thread.currentThread().interrupt();
        }
      }
    }
  }

}
