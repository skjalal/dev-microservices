package com.example.beerorderservice.web.mappers;

import com.example.beerorderservice.domain.BeerOrderLine;
import com.example.brewery.models.BeerOrderLineDTO;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper
@DecoratedWith(value = BeerOrderLineMapperDecorator.class)
public interface BeerOrderLineMapper extends DateMapper {

  BeerOrderLineDTO beerOrderLineToDto(BeerOrderLine line);

  BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDTO dto);
}
