package com.example.beerorderservice.testcomponets;

import com.example.beerorderservice.config.JmsConfig;
import com.example.brewery.events.ValidateOrderRequest;
import com.example.brewery.events.ValidateOrderResult;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BeerOrderValidationListener {

  private final JmsTemplate jmsTemplate;

  @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
  public void list(Message<ValidateOrderRequest> msg) {
    boolean isValid = true;
    boolean sendResponse = true;

    ValidateOrderRequest request = msg.getPayload();

    //condition to fail validation
    if (request.getBeerOrderDTO().getCustomerRef() != null) {
      if (request.getBeerOrderDTO().getCustomerRef().equals("fail-validation")) {
        isValid = false;
      } else if (request.getBeerOrderDTO().getCustomerRef().equals("dont-validate")) {
        sendResponse = false;
      }
    }

    if (sendResponse) {
      jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE,
          ValidateOrderResult.builder().isValid(isValid).orderId(request.getBeerOrderDTO().getId())
              .build());
    }
  }

}
