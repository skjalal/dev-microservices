package com.example.beerorderservice.repositories;

import com.example.beerorderservice.domain.BeerOrderLine;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerOrderLineRepository extends JpaRepository<BeerOrderLine, UUID> {

}
