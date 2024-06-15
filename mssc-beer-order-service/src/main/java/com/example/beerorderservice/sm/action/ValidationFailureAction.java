package com.example.beerorderservice.sm.action;

import com.example.beerorderservice.domain.BeerOrderEventEnum;
import com.example.beerorderservice.domain.BeerOrderStatusEnum;
import com.example.beerorderservice.services.BeerOrderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidationFailureAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

  @Override
  public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> context) {
    String beerOrderId = (String) context.getMessage().getHeaders()
        .get(BeerOrderManager.ORDER_ID_HEADER);
    log.error("Compensating Transaction.... Validation Failed: {}", beerOrderId);
  }
}
