package com.example.beerorderservice.sm.action;

import static com.example.beerorderservice.config.JmsConfig.ALLOCATE_ORDER_QUEUE;
import static com.example.beerorderservice.services.BeerOrderManager.ORDER_ID_HEADER;

import com.example.beerorderservice.domain.BeerOrderEventEnum;
import com.example.beerorderservice.domain.BeerOrderStatusEnum;
import com.example.beerorderservice.repositories.BeerOrderRepository;
import com.example.beerorderservice.web.mappers.BeerOrderMapper;
import com.example.brewery.events.AllocateOrderRequest;
import com.example.brewery.models.BeerOrderDTO;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AllocateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

  private final JmsTemplate jmsTemplate;
  private final BeerOrderRepository beerOrderRepository;
  private final BeerOrderMapper beerOrderMapper;

  @Override
  public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
    Optional.ofNullable(stateContext.getMessage().getHeaders().get(ORDER_ID_HEADER))
        .map(Object::toString).map(UUID::fromString).flatMap(beerOrderRepository::findById)
        .map(beerOrderMapper::beerOrderToDto).ifPresent(this::sendJmsMessage);
  }

  private void sendJmsMessage(BeerOrderDTO dto) {
    jmsTemplate.convertAndSend(ALLOCATE_ORDER_QUEUE,
        AllocateOrderRequest.builder().beerOrderDTO(dto).build());
    log.debug("Sent order allocation request to queue for order id: {}", dto.getId());
  }
}
