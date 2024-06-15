package com.example.msscbeerservice.web.mappers;

import com.example.msscbeerservice.domain.Beer;
import com.example.msscbeerservice.services.inventory.BeerInventoryService;
import com.example.brewery.models.BeerDTO;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Setter(onMethod_ = @Autowired)
public abstract class BeerMapperDecorator implements BeerMapper {

  private BeerInventoryService beerInventoryService;
  private BeerMapper mapper;

  @Override
  public BeerDTO beerToBeerDTO(Beer beer) {
    return mapper.beerToBeerDTO(beer);
  }

  @Override
  public BeerDTO beerToBeerDTOWithInventory(Beer beer) {
    BeerDTO dto = mapper.beerToBeerDTO(beer);
    dto.setQuantityOnHand(beerInventoryService.getOnHandInventory(beer.getId()));
    return dto;
  }

  @Override
  public Beer beerDTOToBeer(BeerDTO beerDto) {
    return mapper.beerDTOToBeer(beerDto);
  }
}
