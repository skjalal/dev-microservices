package com.example.beerorderservice.services.beer.impl;

import com.example.beerorderservice.services.beer.BeerService;
import com.example.brewery.models.BeerDTO;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BeerServiceImpl implements BeerService {

  private final RestTemplate restTemplate;
  private final String host;

  @Autowired
  public BeerServiceImpl(RestTemplateBuilder builder,
      @Value("${app.brewery.beer-service-host}") String host) {
    this.restTemplate = builder.build();
    this.host = host;
  }

  @Override
  public Optional<BeerDTO> getBeerById(UUID uuid) {
    return Optional.ofNullable(
        restTemplate.getForObject(host + "/api/v1/beer/{beerId}", BeerDTO.class, uuid));
  }

  @Override
  public Optional<BeerDTO> getBeerByUpc(String upc) {
    return Optional.ofNullable(
        restTemplate.getForObject(host + "/api/v1/beer/upc/{upc}", BeerDTO.class, upc));
  }
}
