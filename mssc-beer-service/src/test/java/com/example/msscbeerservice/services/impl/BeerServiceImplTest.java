package com.example.msscbeerservice.services.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.example.msscbeerservice.domain.Beer;
import com.example.msscbeerservice.exceptions.NotFoundException;
import com.example.msscbeerservice.repository.BeerRepository;
import com.example.msscbeerservice.services.BeerService;
import com.example.msscbeerservice.web.mappers.BeerMapper;
import com.example.brewery.models.BeerDTO;
import com.example.brewery.models.BeerPagedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@SpringBootTest(classes = {BeerServiceImpl.class})
class BeerServiceImplTest {

  @Autowired
  BeerService beerService;

  @MockBean
  BeerRepository beerRepository;

  @MockBean
  BeerMapper beerMapper;

  Beer beer;
  BeerDTO beerDTO;

  @BeforeEach
  void setUp() {
    beer = Beer.builder().build();
    beerDTO = BeerDTO.builder().build();
  }

  @ParameterizedTest
  @ValueSource(strings = {"byName", "byStyle", "both", "all"})
  void listBeers(String filter) {
    String beerName;
    String beerStyle;
    Page<Beer> beers = new PageImpl<>(List.of(beer));
    if (filter.equals("byName")) {
      beerName = "Beer";
      beerStyle = "";
      when(beerRepository.findAllByBeerName(anyString(), any(PageRequest.class))).thenReturn(beers);
    } else if (filter.equals("byStyle")) {
      beerName = "";
      beerStyle = "ALA";
      when(beerRepository.findAllByBeerStyle(anyString(), any(PageRequest.class))).thenReturn(
          beers);
    } else if (filter.equalsIgnoreCase("both")) {
      beerName = "Beer";
      beerStyle = "ALA";
      when(beerRepository.findAllByBeerNameAndBeerStyle(anyString(), anyString(),
          any(PageRequest.class))).thenReturn(beers);
    } else {
      beerName = "";
      beerStyle = "";
      when(beerRepository.findAll(any(PageRequest.class))).thenReturn(beers);
    }
    when(beerMapper.beerToBeerDTO(any(Beer.class))).thenReturn(beerDTO);
    BeerPagedList list = beerService.listBeers(beerName, beerStyle, PageRequest.of(1, 10), false);
    assertNotNull(list);
    assertNotNull(list.getContent());
    assertFalse(list.getContent().isEmpty());
  }

  @Test
  void listBeerWithInventory() {
    Page<Beer> beers = new PageImpl<>(List.of(beer));
    when(beerRepository.findAll(any(PageRequest.class))).thenReturn(beers);
    when(beerMapper.beerToBeerDTOWithInventory(any(Beer.class))).thenReturn(beerDTO);
    BeerPagedList list = beerService.listBeers("", "", PageRequest.of(1, 10), true);
    assertNotNull(list);
    assertNotNull(list.getContent());
    assertFalse(list.getContent().isEmpty());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getById(boolean showInventoryOnHand) {
    when(beerRepository.findById(any(UUID.class))).thenReturn(Optional.of(beer));
    if (showInventoryOnHand) {
      when(beerMapper.beerToBeerDTOWithInventory(any(Beer.class))).thenReturn(beerDTO);
    } else {
      when(beerMapper.beerToBeerDTO(any(Beer.class))).thenReturn(beerDTO);
    }
    assertNotNull(beerService.getById(UUID.randomUUID(), showInventoryOnHand));
  }

  @Test
  void getByIdNotFoundException() {
    when(beerRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
    UUID id = UUID.randomUUID();
    assertThrows(NotFoundException.class, () -> beerService.getById(id, false));
  }

  @Test
  void getByUpc() {
    when(beerRepository.findByUpc(anyString())).thenReturn(Optional.of(beer));
    when(beerMapper.beerToBeerDTO(any(Beer.class))).thenReturn(beerDTO);
    assertNotNull(beerService.getByUpc("000023211"));
  }

  @Test
  void saveNewBeer() {
    when(beerMapper.beerDTOToBeer(any(BeerDTO.class))).thenReturn(beer);
    doReturn(beer).when(beerRepository).save(any(Beer.class));
    when(beerMapper.beerToBeerDTO(any(Beer.class))).thenReturn(beerDTO);
    assertNotNull(beerService.saveNewBeer(beerDTO));
  }

  @Test
  void updateBeer() {
    when(beerMapper.beerDTOToBeer(any(BeerDTO.class))).thenReturn(beer);
    when(beerRepository.findById(any(UUID.class))).thenReturn(Optional.of(beer));
    doReturn(beer).when(beerRepository).save(any(Beer.class));
    when(beerMapper.beerToBeerDTO(any(Beer.class))).thenReturn(beerDTO);
    assertNotNull(beerService.updateBeer(UUID.randomUUID(), beerDTO));
  }
}