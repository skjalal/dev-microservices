package com.example.beerorderservice.testcomponets;

import com.example.beerorderservice.config.JmsConfig;
import com.example.brewery.events.AllocateOrderRequest;
import com.example.brewery.events.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BeerOrderAllocationListener {

  private final JmsTemplate jmsTemplate;

  @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
  public void listen(Message<AllocateOrderRequest> msg) {
    AllocateOrderRequest request = msg.getPayload();
    boolean pendingInventory = false;
    boolean allocationError = false;
    boolean sendResponse = true;

    //set allocation error
    if (request.getBeerOrderDTO().getCustomerRef() != null) {
      if (request.getBeerOrderDTO().getCustomerRef().equals("fail-allocation")) {
        allocationError = true;
      } else if (request.getBeerOrderDTO().getCustomerRef().equals("partial-allocation")) {
        pendingInventory = true;
      } else if (request.getBeerOrderDTO().getCustomerRef().equals("dont-allocate")) {
        sendResponse = false;
      }
    }

    boolean finalPendingInventory = pendingInventory;

    request.getBeerOrderDTO().getBeerOrderLines().forEach(beerOrderLineDto -> {
      if (finalPendingInventory) {
        beerOrderLineDto.setQuantityAllocated(beerOrderLineDto.getOrderQuantity() - 1);
      } else {
        beerOrderLineDto.setQuantityAllocated(beerOrderLineDto.getOrderQuantity());
      }
    });

    if (sendResponse) {
      jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE,
          AllocateOrderResult.builder().beerOrderDTO(request.getBeerOrderDTO())
              .pendingInventory(pendingInventory).allocationError(allocationError).build());
    }
  }
}
