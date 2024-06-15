package com.example.beerorderservice.repositories;

import com.example.beerorderservice.domain.Customer;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

  List<Customer> findAllByCustomerNameLike(String customerName);
}
