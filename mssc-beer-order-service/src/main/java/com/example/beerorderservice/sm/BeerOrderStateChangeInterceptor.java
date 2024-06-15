package com.example.beerorderservice.sm;

import static com.example.beerorderservice.services.BeerOrderManager.ORDER_ID_HEADER;

import com.example.beerorderservice.domain.BeerOrder;
import com.example.beerorderservice.domain.BeerOrderEventEnum;
import com.example.beerorderservice.domain.BeerOrderStatusEnum;
import com.example.beerorderservice.repositories.BeerOrderRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BeerOrderStateChangeInterceptor extends
    StateMachineInterceptorAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

  private final BeerOrderRepository beerOrderRepository;

  @Override
  public void preStateChange(State<BeerOrderStatusEnum, BeerOrderEventEnum> state,
      Message<BeerOrderEventEnum> message,
      Transition<BeerOrderStatusEnum, BeerOrderEventEnum> transition,
      StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachine,
      StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> rootStateMachine) {
    log.debug("Pre-State Change");
    String orderId = Optional.ofNullable(message).map(this::fetchHeaderValue)
        .orElseGet(String::new);
    log.debug("Saving state for order id: {} Status: {}", orderId, state.getId());
    BeerOrder beerOrder = beerOrderRepository.findById(UUID.fromString(orderId)).orElseThrow();
    beerOrder.setOrderStatus(state.getId());
    BeerOrder saveBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);
    log.debug("Updated BeerOrder: {}", saveBeerOrder.getOrderStatus());
  }

  private String fetchHeaderValue(final Message<BeerOrderEventEnum> message) {
    return message.getHeaders().getOrDefault(ORDER_ID_HEADER, "").toString();
  }
}
