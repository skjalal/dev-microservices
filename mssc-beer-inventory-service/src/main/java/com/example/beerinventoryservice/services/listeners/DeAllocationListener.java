package com.example.beerinventoryservice.services.listeners;

import static com.example.beerinventoryservice.config.JmsConfig.DEALLOCATE_ORDER_QUEUE;

import com.example.beerinventoryservice.services.AllocationService;
import com.example.brewery.events.DeAllocateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DeAllocationListener {

  private final AllocationService allocationService;

  @JmsListener(destination = DEALLOCATE_ORDER_QUEUE)
  public void listen(DeAllocateOrderRequest request) {
    allocationService.deallocateOrder(request.getBeerOrderDTO());
  }
}
