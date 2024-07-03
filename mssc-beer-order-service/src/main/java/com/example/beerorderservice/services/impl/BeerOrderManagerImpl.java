package com.example.beerorderservice.services.impl;

import static com.example.beerorderservice.utils.CommonUtils.awaitForStatus;

import com.example.beerorderservice.domain.BeerOrder;
import com.example.beerorderservice.domain.BeerOrderEventEnum;
import com.example.beerorderservice.domain.BeerOrderStatusEnum;
import com.example.beerorderservice.repositories.BeerOrderRepository;
import com.example.beerorderservice.services.BeerOrderManager;
import com.example.brewery.models.BeerOrderDTO;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BeerOrderManagerImpl implements BeerOrderManager {

  private final BeerOrderRepository beerOrderRepository;
  private final StateMachineService<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachineService;

  @Override
  public BeerOrder newBeerOrder(BeerOrder beerOrder) {
    beerOrder.setId(null);
    beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);
    BeerOrder saveBeerOrder = beerOrderRepository.save(beerOrder);
    sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATE_ORDER);
    return saveBeerOrder;
  }

  @Override
  public void processValidationResult(UUID beerOrderId, Boolean isValid) {
    log.debug("Process Validation Result for beerOrderId: {} Valid? {}", beerOrderId, isValid);

    BeerOrderEventEnum eventEnum =
        isValid.equals(Boolean.TRUE) ? BeerOrderEventEnum.VALIDATION_PASSED
            : BeerOrderEventEnum.VALIDATION_FAILED;

    Optional<BeerOrder> order = beerOrderRepository.findById(beerOrderId);
    if (order.isPresent()) {
      BeerOrder beerOrder = order.get();
      sendBeerOrderEvent(beerOrder, eventEnum);
      if (isValid.equals(Boolean.TRUE)) {
        awaitForStatus(beerOrderRepository, beerOrder.getId(), BeerOrderStatusEnum.VALIDATED);
        beerOrderRepository.findById(beerOrderId)
            .ifPresent(d -> sendBeerOrderEvent(d, BeerOrderEventEnum.ALLOCATE_ORDER));
      }
    } else {
      catchException(beerOrderId);
    }
  }

  @Override
  public void beerOrderAllocationPassed(BeerOrderDTO beerOrderDto) {
    Optional<BeerOrder> order = beerOrderRepository.findById(beerOrderDto.getId());

    if (order.isPresent()) {
      BeerOrder beerOrder = order.get();
      sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_SUCCESS);
      awaitForStatus(beerOrderRepository, beerOrder.getId(), BeerOrderStatusEnum.ALLOCATED);
      updateAllocatedQty(beerOrderDto);
    } else {
      catchException(beerOrderDto.getId());
    }
  }

  @Override
  public void beerOrderAllocationPendingInventory(BeerOrderDTO beerOrderDto) {
    Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderDto.getId());

    if (beerOrderOptional.isPresent()) {
      BeerOrder beerOrder = beerOrderOptional.get();
      sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_NO_INVENTORY);
      awaitForStatus(beerOrderRepository, beerOrder.getId(), BeerOrderStatusEnum.PENDING_INVENTORY);
      updateAllocatedQty(beerOrderDto);
    } else {
      catchException(beerOrderDto.getId());
    }
  }

  @Override
  public void beerOrderAllocationFailed(BeerOrderDTO beerOrderDto) {
    Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderDto.getId());

    if (beerOrderOptional.isPresent()) {
      BeerOrder beerOrder = beerOrderOptional.get();
      sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_FAILED);
    } else {
      catchException(beerOrderDto.getId());
    }
  }

  @Override
  public void beerOrderPickedUp(UUID id) {
    Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(id);

    if (beerOrderOptional.isPresent()) {
      BeerOrder beerOrder = beerOrderOptional.get();
      sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.BEERORDER_PICKED_UP);
    } else {
      catchException(id);
    }
  }

  @Override
  public void cancelOrder(UUID id) {
    beerOrderRepository.findById(id).ifPresentOrElse(
        beerOrder -> sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.CANCEL_ORDER),
        () -> catchException(id));
  }

  private void updateAllocatedQty(BeerOrderDTO beerOrderDto) {
    Optional<BeerOrder> allocatedOrderOptional = beerOrderRepository.findById(beerOrderDto.getId());

    if (allocatedOrderOptional.isPresent()) {
      BeerOrder allocatedOrder = allocatedOrderOptional.get();
      allocatedOrder.getBeerOrderLines()
          .forEach(beerOrderLine -> beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
            if (beerOrderLine.getId().equals(beerOrderLineDto.getId())) {
              beerOrderLine.setQuantityAllocated(beerOrderLineDto.getQuantityAllocated());
            }
          }));
      beerOrderRepository.saveAndFlush(allocatedOrder);
    } else {
      catchException(beerOrderDto.getId());
    }
  }

  private void catchException(UUID id) {
    log.error("Order Not Found. Id: {}", id);
  }

  private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEventEnum eventEnum) {
    StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = stateMachineService.acquireStateMachine(
        String.valueOf(beerOrder.getId()));
    Message<BeerOrderEventEnum> message = MessageBuilder.withPayload(eventEnum)
        .setHeader(ORDER_ID_HEADER, beerOrder.getId().toString()).build();

    log.debug("Sending event: {} of status: {}", eventEnum, beerOrder.getOrderStatus());
    sm.sendEvent(Mono.just(message)).subscribe();
  }
}
