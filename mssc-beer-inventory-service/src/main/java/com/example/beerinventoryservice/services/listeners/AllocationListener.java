package com.example.beerinventoryservice.services.listeners;

import static com.example.beerinventoryservice.config.JmsConfig.ALLOCATE_ORDER_QUEUE;
import static com.example.beerinventoryservice.config.JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE;

import com.example.beerinventoryservice.services.AllocationService;
import com.example.brewery.events.AllocateOrderRequest;
import com.example.brewery.events.AllocateOrderResult;
import com.example.brewery.events.AllocateOrderResult.AllocateOrderResultBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AllocationListener {

  private final AllocationService allocationService;
  private final JmsTemplate jmsTemplate;

  @JmsListener(destination = ALLOCATE_ORDER_QUEUE)
  public void listen(AllocateOrderRequest request) {
    AllocateOrderResultBuilder builder = AllocateOrderResult.builder();
    builder.beerOrderDTO(request.getBeerOrderDTO());
    try {
      builder.pendingInventory(
          allocationService.allocateOrder(request.getBeerOrderDTO()).equals(Boolean.FALSE));
      builder.allocationError(Boolean.FALSE);
    } catch (Exception e) {
      log.error("Allocation failed for order id: {}", request.getBeerOrderDTO().getId(), e);
      builder.allocationError(Boolean.TRUE);
    }
    jmsTemplate.convertAndSend(ALLOCATE_ORDER_RESPONSE_QUEUE, builder.build());
  }
}
