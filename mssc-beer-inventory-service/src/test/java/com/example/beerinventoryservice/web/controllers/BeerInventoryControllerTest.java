package com.example.beerinventoryservice.web.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.beerinventoryservice.domain.BeerInventory;
import com.example.beerinventoryservice.repositories.BeerInventoryRepository;
import com.example.beerinventoryservice.web.mappers.BeerInventoryMapper;
import com.example.brewery.models.BeerInventoryDTO;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {BeerInventoryController.class})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.beer.com", uriPort = 80)
class BeerInventoryControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  BeerInventoryRepository beerInventoryRepository;

  @MockBean
  BeerInventoryMapper beerInventoryMapper;

  UUID id;
  BeerInventory beerInventory;
  BeerInventoryDTO beerInventoryDTO;

  @BeforeEach
  void setUp() {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    id = UUID.randomUUID();
    beerInventory = BeerInventory.builder().id(id).beerId(id).upc("0000232").version(1L)
        .quantityOnHand(2).createdDate(timestamp).lastModifiedDate(timestamp).build();
    beerInventoryDTO = BeerInventoryDTO.builder().id(id).beerId(id).quantityOnHand(2)
        .createdDate(OffsetDateTime.now()).lastModifiedDate(OffsetDateTime.now()).build();
  }

  @Test
  void listBeersById() throws Exception {
    when(beerInventoryRepository.findAllByBeerId(any(UUID.class))).thenReturn(
        List.of(beerInventory));
    when(beerInventoryMapper.beerInventoryToBeerInventoryDto(any(BeerInventory.class))).thenReturn(
        beerInventoryDTO);

    mockMvc.perform(
            get("/api/v1/beer/{beerId}/inventory", id.toString()).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andDo(document("get-inventory",
            pathParameters(parameterWithName("beerId").description("UUID of desired beer.")),
            responseFields(fieldWithPath("[].id").description("ID of beer response"),
                fieldWithPath("[].beerId").description("ID of beer"),
                fieldWithPath("[].quantityOnHand").description("Quantity of beer"),
                fieldWithPath("[].createdDate").description("Creation date of beer"),
                fieldWithPath("[].lastModifiedDate").description("Last modify date date of beer"))));
  }
}