package com.example.beerorderservice.web.mappers;

import com.example.beerorderservice.domain.BeerOrderLine;
import com.example.beerorderservice.services.beer.BeerService;
import com.example.brewery.models.BeerDTO;
import com.example.brewery.models.BeerOrderLineDTO;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Setter(onMethod_ = @Autowired)
public abstract class BeerOrderLineMapperDecorator implements BeerOrderLineMapper {

  BeerService beerService;
  BeerOrderLineMapper beerOrderLineMapper;

  @Override
  public BeerOrderLineDTO beerOrderLineToDto(BeerOrderLine line) {
    BeerOrderLineDTO orderLineDto = beerOrderLineMapper.beerOrderLineToDto(line);
    return beerService.getBeerByUpc(line.getUpc()).map(d -> toDTO(d, orderLineDto))
        .orElse(orderLineDto);
  }

  private BeerOrderLineDTO toDTO(BeerDTO beerDTO, BeerOrderLineDTO orderLineDTO) {
    orderLineDTO.setBeerName(beerDTO.getBeerName());
    orderLineDTO.setBeerStyle(beerDTO.getBeerStyle());
    orderLineDTO.setPrice(beerDTO.getPrice());
    orderLineDTO.setBeerId(beerDTO.getId());
    return orderLineDTO;
  }
}
