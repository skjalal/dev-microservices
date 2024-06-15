package com.example.msscbeerservice.services.impl;

import com.example.msscbeerservice.domain.Beer;
import com.example.msscbeerservice.repository.BeerRepository;
import com.example.msscbeerservice.services.BeerService;
import com.example.msscbeerservice.utils.BeanCopyUtils;
import com.example.msscbeerservice.web.mappers.BeerMapper;
import com.example.brewery.models.BeerDTO;
import com.example.brewery.models.BeerPagedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BeerServiceImpl implements BeerService {

  private final BeerRepository beerRepository;
  private final BeerMapper beerMapper;

  @Override
  @Cacheable(cacheNames = {"beerListCache"}, condition = "#showInventoryOnHand == false ")
  public BeerPagedList listBeers(String beerName, String beerStyle, PageRequest pageRequest,
      boolean showInventoryOnHand) {
    Page<Beer> beerPage;
    if (Stream.of(beerName, beerStyle).allMatch(StringUtils::hasText)) {
      beerPage = beerRepository.findAllByBeerNameAndBeerStyle(beerName, beerStyle, pageRequest);
    } else if (StringUtils.hasText(beerName)) {
      beerPage = beerRepository.findAllByBeerName(beerName, pageRequest);
    } else if (StringUtils.hasText(beerStyle)) {
      beerPage = beerRepository.findAllByBeerStyle(beerStyle, pageRequest);
    } else {
      beerPage = beerRepository.findAll(pageRequest);
    }
    List<BeerDTO> contents;
    if (showInventoryOnHand) {
      contents = beerPage.map(beerMapper::beerToBeerDTOWithInventory).toList();
    } else {
      contents = beerPage.map(beerMapper::beerToBeerDTO).toList();
    }
    PageRequest pageDetails = PageRequest.of(pageRequest.getPageNumber(),
        pageRequest.getPageSize());
    return new BeerPagedList(contents, pageDetails, beerPage.getTotalElements());
  }

  @Override
  @Cacheable(cacheNames = {"beerCache"}, key = "#id", condition = "#showInventoryOnHand == false ")
  public BeerDTO getById(UUID id, boolean showInventoryOnHand) {
    Beer beer = beerRepository.findById(id).orElseThrow(this::toDefaultThrow);
    BeerDTO beerDTO;
    if (showInventoryOnHand) {
      beerDTO = beerMapper.beerToBeerDTOWithInventory(beer);
    } else {
      beerDTO = beerMapper.beerToBeerDTO(beer);
    }
    return beerDTO;
  }

  @Override
  @Cacheable(cacheNames = {"beerUpcCache"}, key = "#upc")
  public BeerDTO getByUpc(String upc) {
    return beerRepository.findByUpc(upc).map(beerMapper::beerToBeerDTO)
        .orElseThrow(this::toDefaultThrow);
  }

  @Override
  public BeerDTO saveNewBeer(BeerDTO beerDTO) {
    return beerMapper.beerToBeerDTO(beerRepository.save(beerMapper.beerDTOToBeer(beerDTO)));
  }

  @Override
  public BeerDTO updateBeer(UUID id, BeerDTO beerDTO) {
    Beer source = beerMapper.beerDTOToBeer(beerDTO);
    Beer destination = beerRepository.findById(id).orElseThrow(this::toDefaultThrow);
    BeanCopyUtils.copyNonNullProperties(source, destination);
    return beerMapper.beerToBeerDTO(beerRepository.save(destination));
  }
}
