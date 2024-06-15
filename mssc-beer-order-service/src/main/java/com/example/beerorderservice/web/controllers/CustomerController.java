package com.example.beerorderservice.web.controllers;

import com.example.beerorderservice.services.CustomerService;
import com.example.brewery.models.CustomerPagedList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
public class CustomerController {

  private final CustomerService customerService;

  @GetMapping(value = {"", "/"})
  public CustomerPagedList listCustomers(
      @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
      @RequestParam(value = "pageSize", defaultValue = "25") Integer pageSize) {
    return customerService.listCustomers(PageRequest.of(pageNumber, pageSize));
  }
}
