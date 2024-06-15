package com.example.beerorderservice.sm.action;

import static com.example.beerorderservice.config.JmsConfig.DEALLOCATE_ORDER_QUEUE;
import static com.example.beerorderservice.services.BeerOrderManager.ORDER_ID_HEADER;

import com.example.beerorderservice.domain.BeerOrder;
import com.example.beerorderservice.domain.BeerOrderEventEnum;
import com.example.beerorderservice.domain.BeerOrderStatusEnum;
import com.example.beerorderservice.repositories.BeerOrderRepository;
import com.example.beerorderservice.web.mappers.BeerOrderMapper;
import com.example.brewery.events.DeAllocateOrderRequest;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeAllocateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

  private final JmsTemplate jmsTemplate;
  private final BeerOrderRepository beerOrderRepository;
  private final BeerOrderMapper beerOrderMapper;

  @Override
  public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> context) {
    String beerOrderId = Optional.ofNullable(context.getMessage().getHeaders().get(ORDER_ID_HEADER))
        .map(Object::toString).orElseGet(String::new);
    Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(
        UUID.fromString(beerOrderId));

    beerOrderOptional.ifPresentOrElse(beerOrder -> {
      jmsTemplate.convertAndSend(DEALLOCATE_ORDER_QUEUE,
          DeAllocateOrderRequest.builder().beerOrderDTO(beerOrderMapper.beerOrderToDto(beerOrder))
              .build());
      log.debug("Sent DeAllocation Request for order id: {}", beerOrderId);
    }, () -> log.error("Beer Order Not Found!"));
  }
}
