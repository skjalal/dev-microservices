package com.example.beerorderservice.web.mappers;

import com.example.beerorderservice.domain.BeerOrder;
import com.example.brewery.models.BeerOrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = {BeerOrderLineMapper.class})
public interface BeerOrderMapper {

  @Mappings(value = {@Mapping(source = "customer.id", target = "customerId")})
  BeerOrderDTO beerOrderToDto(BeerOrder beerOrder);

  BeerOrder dtoToBeerOrder(BeerOrderDTO dto);
}
