package com.example.beerorderservice.sm.action;

import static com.example.beerorderservice.config.JmsConfig.ALLOCATE_FAILURE_QUEUE;
import static com.example.beerorderservice.services.BeerOrderManager.ORDER_ID_HEADER;

import com.example.beerorderservice.domain.BeerOrderEventEnum;
import com.example.beerorderservice.domain.BeerOrderStatusEnum;
import com.example.brewery.events.AllocationFailureEvent;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationFailureAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

  private final JmsTemplate jmsTemplate;

  @Override
  public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> context) {
    String beerOrderId = Optional.ofNullable(context.getMessage().getHeaders().get(ORDER_ID_HEADER))
        .map(Object::toString).orElseGet(String::new);

    jmsTemplate.convertAndSend(ALLOCATE_FAILURE_QUEUE,
        AllocationFailureEvent.builder().orderId(UUID.fromString(beerOrderId)).build());

    log.debug("Sent Allocation Failure Message to queue for order id {}", beerOrderId);
  }
}
