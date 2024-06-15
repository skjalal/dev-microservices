package com.example.beerinventoryservice.repositories;

import com.example.beerinventoryservice.domain.BeerInventory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerInventoryRepository extends JpaRepository<BeerInventory, UUID> {

  List<BeerInventory> findAllByBeerId(UUID beerId);

  List<BeerInventory> findAllByUpc(String upc);
}
