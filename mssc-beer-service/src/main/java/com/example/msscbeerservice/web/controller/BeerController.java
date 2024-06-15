package com.example.msscbeerservice.web.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.example.msscbeerservice.services.BeerService;
import com.example.brewery.models.BeerDTO;
import com.example.brewery.models.BeerPagedList;
import com.example.brewery.models.BeerQueryParams;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = {"/api/v1/beer"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BeerController {

  private final BeerService beerService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = {"", "/"})
  public ResponseEntity<BeerPagedList> listBeers(BeerQueryParams queryParams) {
    String beerName = Optional.ofNullable(queryParams.getBeerName()).orElseGet(String::new);
    String beerStyle = Optional.ofNullable(queryParams.getBeerStyle()).map(Enum::name)
        .orElseGet(String::new);
    Integer pageSize = queryParams.getPageSize();
    Integer pageNumber = queryParams.getPageNumber();
    Boolean showInventoryOnHand = queryParams.getShowInventoryOnHand();
    BeerPagedList beerList = beerService.listBeers(beerName, beerStyle,
        PageRequest.of(pageNumber, pageSize), showInventoryOnHand);
    return ResponseEntity.ok(beerList);
  }

  @GetMapping(value = {"/{beerId}"})
  public ResponseEntity<BeerDTO> getBeerById(@PathVariable(value = "beerId") UUID id,
      @RequestParam(name = "showInventoryOnHand", defaultValue = "false") Boolean showInventoryOnHand) {
    return ResponseEntity.ok(beerService.getById(id, showInventoryOnHand));
  }

  @GetMapping(value = {"/upc/{upc}"})
  public ResponseEntity<BeerDTO> getBeerByUpc(@PathVariable(name = "upc") String upc) {
    return ResponseEntity.ok(beerService.getByUpc(upc));
  }

  @PostMapping(value = {"", "/"})
  public ResponseEntity<Object> saveNewBeer(@RequestBody @Validated BeerDTO beerDTO) {
    return new ResponseEntity<>(beerService.saveNewBeer(beerDTO), CREATED);
  }

  @PutMapping(value = {"/{beerId}"})
  public ResponseEntity<Object> updateBeer(@PathVariable(value = "beerId") UUID id,
      @RequestBody @Validated BeerDTO beerDTO) {
    return new ResponseEntity<>(beerService.updateBeer(id, beerDTO), NO_CONTENT);
  }
}
