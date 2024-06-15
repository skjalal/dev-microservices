package com.example.beerorderservice.web.controllers;

import com.example.beerorderservice.services.BeerOrderService;
import com.example.brewery.models.BeerOrderDTO;
import com.example.brewery.models.BeerOrderPagedList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(value = {"/api/v1/customers/{customerId}/"})
public class BeerOrderController {

  private final BeerOrderService beerOrderService;

  @GetMapping(value = {"orders"})
  public BeerOrderPagedList listOrders(@PathVariable(name = "customerId") UUID customerId,
      @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
      @RequestParam(value = "pageSize", defaultValue = "25") Integer pageSize) {
    return beerOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
  }

  @PostMapping(value = {"orders"})
  @ResponseStatus(HttpStatus.CREATED)
  public BeerOrderDTO placeOrder(@PathVariable(name = "customerId") UUID customerId,
      @RequestBody BeerOrderDTO beerOrderDto) {
    return beerOrderService.placeOrder(customerId, beerOrderDto);
  }

  @GetMapping(value = {"orders/{orderId}"})
  public BeerOrderDTO getOrder(@PathVariable(name = "customerId") UUID customerId,
      @PathVariable(name = "orderId") UUID orderId) {
    return beerOrderService.getOrderById(customerId, orderId);
  }

  @PutMapping(value = {"orders/{orderId}/pickup"})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void pickupOrder(@PathVariable(name = "customerId") UUID customerId,
      @PathVariable(name = "orderId") UUID orderId) {
    beerOrderService.pickupOrder(customerId, orderId);
  }
}
