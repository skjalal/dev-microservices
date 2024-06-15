package com.example.beerorderservice.sm;

import static com.example.beerorderservice.domain.BeerOrderEventEnum.ALLOCATE_ORDER;
import static com.example.beerorderservice.domain.BeerOrderEventEnum.ALLOCATION_FAILED;
import static com.example.beerorderservice.domain.BeerOrderEventEnum.ALLOCATION_NO_INVENTORY;
import static com.example.beerorderservice.domain.BeerOrderEventEnum.ALLOCATION_SUCCESS;
import static com.example.beerorderservice.domain.BeerOrderEventEnum.BEERORDER_PICKED_UP;
import static com.example.beerorderservice.domain.BeerOrderEventEnum.CANCEL_ORDER;
import static com.example.beerorderservice.domain.BeerOrderEventEnum.VALIDATE_ORDER;
import static com.example.beerorderservice.domain.BeerOrderEventEnum.VALIDATION_FAILED;
import static com.example.beerorderservice.domain.BeerOrderEventEnum.VALIDATION_PASSED;
import static com.example.beerorderservice.domain.BeerOrderStatusEnum.ALLOCATED;
import static com.example.beerorderservice.domain.BeerOrderStatusEnum.ALLOCATION_EXCEPTION;
import static com.example.beerorderservice.domain.BeerOrderStatusEnum.ALLOCATION_PENDING;
import static com.example.beerorderservice.domain.BeerOrderStatusEnum.CANCELLED;
import static com.example.beerorderservice.domain.BeerOrderStatusEnum.DELIVERED;
import static com.example.beerorderservice.domain.BeerOrderStatusEnum.DELIVERY_EXCEPTION;
import static com.example.beerorderservice.domain.BeerOrderStatusEnum.NEW;
import static com.example.beerorderservice.domain.BeerOrderStatusEnum.PENDING_INVENTORY;
import static com.example.beerorderservice.domain.BeerOrderStatusEnum.PICKED_UP;
import static com.example.beerorderservice.domain.BeerOrderStatusEnum.VALIDATED;
import static com.example.beerorderservice.domain.BeerOrderStatusEnum.VALIDATION_EXCEPTION;
import static com.example.beerorderservice.domain.BeerOrderStatusEnum.VALIDATION_PENDING;

import com.example.beerorderservice.domain.BeerOrderEventEnum;
import com.example.beerorderservice.domain.BeerOrderStatusEnum;
import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BeerOrderStateMachineConfig extends
    EnumStateMachineConfigurerAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

  private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> validateOrderAction;
  private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> allocateOrderAction;
  private final Action<BeerOrderStatusEnum, BeerOrderEventEnum>  validationFailureAction;
  private final Action<BeerOrderStatusEnum, BeerOrderEventEnum>  allocationFailureAction;
  private final Action<BeerOrderStatusEnum, BeerOrderEventEnum>  deAllocateOrderAction;

  @Override
  public void configure(StateMachineStateConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> states)
      throws Exception {
    states.withStates().initial(NEW).states(EnumSet.allOf(BeerOrderStatusEnum.class)).end(PICKED_UP)
        .end(DELIVERED).end(CANCELLED).end(DELIVERY_EXCEPTION).end(VALIDATION_EXCEPTION).end(ALLOCATION_EXCEPTION);
  }

  @Override
  public void configure(
      StateMachineTransitionConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> transitions)
      throws Exception {
    transitions.withExternal()
        .source(NEW).target(VALIDATION_PENDING).event(VALIDATE_ORDER)
        .action(validateOrderAction).and().withExternal()
        .source(VALIDATION_PENDING).target(VALIDATED).event(VALIDATION_PASSED)
        .and().withExternal()
        .source(VALIDATION_PENDING).target(CANCELLED).event(CANCEL_ORDER)
        .and().withExternal()
        .source(VALIDATION_PENDING).target(VALIDATION_EXCEPTION).event(VALIDATION_FAILED).action(validationFailureAction)
        .and().withExternal()
        .source(VALIDATED).target(ALLOCATION_PENDING).event(ALLOCATE_ORDER).action(allocateOrderAction)
        .and().withExternal()
        .source(VALIDATED).target(CANCELLED).event(CANCEL_ORDER)
        .and().withExternal()
        .source(ALLOCATION_PENDING).target(ALLOCATED).event(ALLOCATION_SUCCESS)
        .and().withExternal()
        .source(ALLOCATION_PENDING).target(ALLOCATION_EXCEPTION).event(ALLOCATION_FAILED).action(allocationFailureAction)
        .and().withExternal()
        .source(ALLOCATION_PENDING).target(CANCELLED).event(CANCEL_ORDER)
        .and().withExternal()
        .source(ALLOCATION_PENDING).target(PENDING_INVENTORY).event(ALLOCATION_NO_INVENTORY)
        .and().withExternal()
        .source(ALLOCATED).target(PICKED_UP).event(BEERORDER_PICKED_UP)
        .and().withExternal()
        .source(ALLOCATED).target(CANCELLED).event(CANCEL_ORDER).action(deAllocateOrderAction);
  }
}
