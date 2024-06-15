package com.example.beerorderservice.services;

import com.example.brewery.models.CustomerPagedList;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

  CustomerPagedList listCustomers(Pageable pageable);
}
