package com.example.beerorderservice.services.impl;

import com.example.beerorderservice.repositories.CustomerRepository;
import com.example.beerorderservice.services.CustomerService;
import com.example.beerorderservice.web.mappers.CustomerMapper;
import com.example.brewery.models.CustomerDTO;
import com.example.brewery.models.CustomerPagedList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  @Override
  public CustomerPagedList listCustomers(Pageable pageable) {
    Page<CustomerDTO> customerPage = customerRepository.findAll(pageable)
        .map(customerMapper::customerToDto);
    return new CustomerPagedList(customerPage.getContent(),
        PageRequest.of(customerPage.getPageable().getPageNumber(),
            customerPage.getPageable().getPageSize()), customerPage.getTotalElements());
  }
}
