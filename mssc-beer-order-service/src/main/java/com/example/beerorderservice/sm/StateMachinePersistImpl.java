package com.example.beerorderservice.sm;

import com.example.beerorderservice.domain.BeerOrder;
import com.example.beerorderservice.domain.BeerOrderEventEnum;
import com.example.beerorderservice.domain.BeerOrderStatusEnum;
import com.example.beerorderservice.repositories.BeerOrderRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptor;

@Slf4j
@RequiredArgsConstructor
public class StateMachinePersistImpl implements
    StateMachineRuntimePersister<BeerOrderStatusEnum, BeerOrderEventEnum, String> {

  private final BeerOrderRepository beerOrderRepository;

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
    BeerOrderStatusEnum statusEnum = findById(uuid)
        .map(BeerOrder::getOrderStatus).orElse(BeerOrderStatusEnum.NEW);
    log.debug("Getting the beer state : {}", statusEnum);
    return new DefaultStateMachineContext<>(statusEnum, null, null, null);
  }

  @Override
  public StateMachineInterceptor<BeerOrderStatusEnum, BeerOrderEventEnum> getInterceptor() {
    return new BeerOrderStateChangeInterceptor(beerOrderRepository);
  }

  private Optional<BeerOrder> findById(String uuid) {
    return Optional.ofNullable(uuid).map(UUID::fromString).flatMap(beerOrderRepository::findById);
  }
}
