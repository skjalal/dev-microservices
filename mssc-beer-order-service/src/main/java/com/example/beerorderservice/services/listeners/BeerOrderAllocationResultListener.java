package com.example.beerorderservice.services.listeners;

import static com.example.beerorderservice.config.JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE;

import com.example.beerorderservice.services.BeerOrderManager;
import com.example.beerorderservice.utils.CommonUtils;
import com.example.brewery.events.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BeerOrderAllocationResultListener {

  private final BeerOrderManager beerOrderManager;
  private final Environment environment;

  @JmsListener(destination = ALLOCATE_ORDER_RESPONSE_QUEUE)
  public void listen(AllocateOrderResult result) {
    CommonUtils.sleep(500L, environment);
    if (result.getAllocationError().equals(Boolean.TRUE)) {
      //allocation error
      beerOrderManager.beerOrderAllocationFailed(result.getBeerOrderDTO());
    } else if (result.getPendingInventory().equals(Boolean.TRUE)) {
      //pending inventory
      beerOrderManager.beerOrderAllocationPendingInventory(result.getBeerOrderDTO());
    } else {
      //allocated normally
      beerOrderManager.beerOrderAllocationPassed(result.getBeerOrderDTO());
    }
  }
}
