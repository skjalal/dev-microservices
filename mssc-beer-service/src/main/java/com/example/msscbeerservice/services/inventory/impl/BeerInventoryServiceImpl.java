package com.example.msscbeerservice.services.inventory.impl;

import com.example.msscbeerservice.services.inventory.BeerInventoryService;
import com.example.msscbeerservice.services.inventory.model.BeerInventoryDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class BeerInventoryServiceImpl implements BeerInventoryService {

  private final RestTemplate restTemplate;
  private final String host;

  @Autowired
  public BeerInventoryServiceImpl(RestTemplateBuilder builder,
      @Value("${app.brewery.inventory-api}") String host) {
    this.restTemplate = builder.build();
    this.host = host;
  }

  @Override
  public Integer getOnHandInventory(UUID beerId) {
    String url = String.format("%s%s", host, "/api/v1/beer/{beerId}/inventory");
    ResponseEntity<List<BeerInventoryDTO>> response = restTemplate.exchange(url, HttpMethod.GET,
        null, new ParameterizedTypeReference<>() {
        }, beerId);
    log.debug("Fetch response: {}", response.getStatusCode());
    List<BeerInventoryDTO> inventories = Optional.ofNullable(response.getBody())
        .orElseGet(List::of);
    return inventories.stream().mapToInt(BeerInventoryDTO::getQuantityOnHand).sum();
  }
}
