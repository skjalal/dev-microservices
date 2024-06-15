package com.example.msscbeerservice.services.order;

import static com.example.msscbeerservice.config.JmsConfig.VALIDATE_ORDER_QUEUE;
import static com.example.msscbeerservice.config.JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE;

import com.example.brewery.events.ValidateOrderRequest;
import com.example.brewery.events.ValidateOrderResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BeerOrderValidationListener {

  private final BeerOrderValidator validator;
  private final JmsTemplate jmsTemplate;

  @JmsListener(destination = VALIDATE_ORDER_QUEUE)
  public void listen(ValidateOrderRequest validateOrderRequest) {
    Boolean isValid = validator.validateOrder(validateOrderRequest.getBeerOrderDTO());
    jmsTemplate.convertAndSend(VALIDATE_ORDER_RESPONSE_QUEUE,
        ValidateOrderResult.builder().orderId(validateOrderRequest.getBeerOrderDTO().getId())
            .isValid(isValid).build());
  }
}
