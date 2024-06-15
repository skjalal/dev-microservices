package com.example.beerorderservice.repositories;

import com.example.beerorderservice.domain.BeerOrder;
import com.example.beerorderservice.domain.Customer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerOrderRepository extends JpaRepository<BeerOrder, UUID> {

  Page<BeerOrder> findAllByCustomer(Customer customer, Pageable pageable);

  Optional<BeerOrder> findByIdAndCustomer(UUID id, Customer customer);
}
