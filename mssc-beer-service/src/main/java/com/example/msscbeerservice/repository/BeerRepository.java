package com.example.msscbeerservice.repository;

import com.example.msscbeerservice.domain.Beer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerRepository extends JpaRepository<Beer, UUID> {

  Page<Beer> findAllByBeerName(String beerName, Pageable pageable);

  Page<Beer> findAllByBeerStyle(String beerStyle, Pageable pageable);

  Page<Beer> findAllByBeerNameAndBeerStyle(String beerName, String beerStyle, Pageable pageable);

  Optional<Beer> findByUpc(String upc);
}
