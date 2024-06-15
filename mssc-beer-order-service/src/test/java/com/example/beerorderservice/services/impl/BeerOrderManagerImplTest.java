package com.example.beerorderservice.services.impl;

import static com.example.beerorderservice.domain.BeerOrderStatusEnum.VALIDATED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.beerorderservice.domain.BeerOrder;
import com.example.beerorderservice.domain.BeerOrderEventEnum;
import com.example.beerorderservice.domain.BeerOrderStatusEnum;
import com.example.beerorderservice.repositories.BeerOrderRepository;
import com.example.beerorderservice.services.BeerOrderManager;
import com.example.beerorderservice.sm.BeerOrderStateChangeInterceptor;
import com.example.brewery.models.BeerOrderDTO;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.access.StateMachineAccess;
import org.springframework.statemachine.access.StateMachineAccessor;
import org.springframework.statemachine.config.StateMachineFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest(classes = {BeerOrderManagerImpl.class})
class BeerOrderManagerImplTest {

  private static final String MESSAGE = "NO_RECORD_FOUND";

  @Autowired
  BeerOrderManager beerOrderManager;

  @MockBean
  StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachineFactory;

  @MockBean
  BeerOrderRepository beerOrderRepository;

  @MockBean
  BeerOrderStateChangeInterceptor interceptor;

  @Mock
  StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm;

  @Mock
  StateMachineAccessor<BeerOrderStatusEnum, BeerOrderEventEnum> asm;

  Consumer<StateMachineAccess<BeerOrderStatusEnum, BeerOrderEventEnum>> sma;

  BeerOrderDTO beerOrderDTO;

  @BeforeEach
  void setUp() {
    sma = csma -> csma.addStateMachineInterceptor(interceptor);
    beerOrderDTO = BeerOrderDTO.builder().id(UUID.randomUUID()).build();
  }

  @Test
  void testProcessValidationResult() {
    BeerOrder beerOrder = BeerOrder.builder().id(UUID.randomUUID()).orderStatus(VALIDATED).build();
    doReturn(Optional.of(beerOrder)).when(beerOrderRepository).findById(any(UUID.class));
    doReturn(sm).when(stateMachineFactory).getStateMachine(any(UUID.class));
    doReturn(Mono.empty()).when(sm).stopReactively();
    doReturn(asm).when(sm).getStateMachineAccessor();
    doNothing().when(asm).doWithAllRegions(sma);
    doReturn(Mono.empty()).when(sm).startReactively();
    doReturn(Flux.empty()).when(sm).sendEvent(ArgumentMatchers.<Mono<Message<BeerOrderEventEnum>>>any());

    beerOrderManager.processValidationResult(UUID.randomUUID(), true);
    verify(beerOrderRepository, times(3)).findById(any(UUID.class));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  @DisplayName(value = MESSAGE)
  void testProcessValidationResult(boolean isValid) {
    doReturn(Optional.empty()).when(beerOrderRepository).findById(any(UUID.class));
    beerOrderManager.processValidationResult(UUID.randomUUID(), isValid);
    verify(beerOrderRepository, times(1)).findById(any(UUID.class));
  }

  @Test
  @DisplayName(value = MESSAGE)
  void testBeerOrderAllocationPassed() {
    doReturn(Optional.empty()).when(beerOrderRepository).findById(any(UUID.class));
    beerOrderManager.beerOrderAllocationPassed(beerOrderDTO);
    verify(beerOrderRepository, times(1)).findById(any(UUID.class));
  }

  @Test
  @DisplayName(value = MESSAGE)
  void testBeerOrderAllocationPendingInventory() {
    doReturn(Optional.empty()).when(beerOrderRepository).findById(any(UUID.class));
    beerOrderManager.beerOrderAllocationPendingInventory(beerOrderDTO);
    verify(beerOrderRepository, times(1)).findById(any(UUID.class));
  }

  @Test
  @DisplayName(value = MESSAGE)
  void testBeerOrderAllocationFailed() {
    doReturn(Optional.empty()).when(beerOrderRepository).findById(any(UUID.class));
    beerOrderManager.beerOrderAllocationFailed(beerOrderDTO);
    verify(beerOrderRepository, times(1)).findById(any(UUID.class));
  }

  @Test
  @DisplayName(value = MESSAGE)
  void testBeerOrderPickedUp() {
    doReturn(Optional.empty()).when(beerOrderRepository).findById(any(UUID.class));
    beerOrderManager.beerOrderPickedUp(UUID.randomUUID());
    verify(beerOrderRepository, times(1)).findById(any(UUID.class));
  }

  @Test
  @DisplayName(value = MESSAGE)
  void testCancelOrder() {
    doReturn(Optional.empty()).when(beerOrderRepository).findById(any(UUID.class));
    beerOrderManager.cancelOrder(UUID.randomUUID());
    verify(beerOrderRepository, times(1)).findById(any(UUID.class));
  }
}