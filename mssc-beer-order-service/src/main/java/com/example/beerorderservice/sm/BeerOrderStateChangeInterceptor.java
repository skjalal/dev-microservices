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
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.persist.AbstractPersistingStateMachineInterceptor;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptor;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

@Slf4j
@Component(value = "persistInterceptor")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BeerOrderStateChangeInterceptor extends
    AbstractPersistingStateMachineInterceptor<BeerOrderStatusEnum, BeerOrderEventEnum, String> implements
    StateMachineRuntimePersister<BeerOrderStatusEnum, BeerOrderEventEnum, String> {

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

  @Override
  public void write(StateMachineContext<BeerOrderStatusEnum, BeerOrderEventEnum> context,
      String uuid) {
    BeerOrder beerOrder = findById(uuid).orElseGet(BeerOrder::new);
    beerOrder.setId(UUID.fromString(uuid));
    beerOrder.setOrderStatus(context.getState());
    beerOrderRepository.saveAndFlush(beerOrder);
    log.debug("Updating the beer state : {}", beerOrder.getOrderStatus());
  }

  @Override
  public StateMachineContext<BeerOrderStatusEnum, BeerOrderEventEnum> read(String uuid) {
    BeerOrderStatusEnum statusEnum = findById(uuid).map(BeerOrder::getOrderStatus)
        .orElse(BeerOrderStatusEnum.NEW);
    log.debug("Getting the beer state : {}", statusEnum);
    return new DefaultStateMachineContext<>(statusEnum, null, null, null);
  }

  @Override
  public StateMachineInterceptor<BeerOrderStatusEnum, BeerOrderEventEnum> getInterceptor() {
    return this;
  }

  private Optional<BeerOrder> findById(String uuid) {
    return Optional.ofNullable(uuid).map(UUID::fromString).flatMap(beerOrderRepository::findById);
  }

  private String fetchHeaderValue(final Message<BeerOrderEventEnum> message) {
    return message.getHeaders().getOrDefault(ORDER_ID_HEADER, "").toString();
  }
}
