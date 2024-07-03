package com.example.beerorderservice.services.listeners;

import static com.example.beerorderservice.config.JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE;

import com.example.beerorderservice.services.BeerOrderManager;
import com.example.beerorderservice.utils.CommonUtils;
import com.example.brewery.events.ValidateOrderResult;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ValidationResultListener {

  private final BeerOrderManager beerOrderManager;
  private final Environment environment;

  @JmsListener(destination = VALIDATE_ORDER_RESPONSE_QUEUE)
  public void listen(ValidateOrderResult result) {
    final UUID orderId = result.getOrderId();
    CommonUtils.sleep(500L, environment);
    log.debug("Validation result for order id: {}", orderId);
    beerOrderManager.processValidationResult(orderId, result.getIsValid());
  }
}
