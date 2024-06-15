package com.example.msscbeerservice.web.controller;

import static com.example.brewery.models.BeerStyleEnum.ALE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.msscbeerservice.services.BeerService;
import com.example.brewery.models.BeerDTO;
import com.example.brewery.models.BeerPagedList;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {BeerController.class})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.beer.com", uriPort = 80)
class BeerControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  BeerService beerService;

  String beerJson;
  ConstrainedFields fields;
  BeerDTO beer;

  @BeforeEach
  void setUp() throws IOException {
    beerJson = Files.readString(Path.of("src/test/resources/stub/beer.json"));
    fields = new ConstrainedFields(BeerDTO.class);
    beer = BeerDTO.builder().id(UUID.randomUUID()).version(1).createdDate(OffsetDateTime.now())
        .lastModifiedDate(OffsetDateTime.now()).beerName("My Beer").beerStyle(ALE)
        .price(new BigDecimal("2.99")).quantityOnHand(12).upc("00087675777575").build();
  }

  @ParameterizedTest
  @ValueSource(strings = {"empty", "default", "paged", "paged1"})
  void getBeer(String data) throws Exception {
    BeerPagedList pagedList;
    ResponseFieldsSnippet snippet;
    String docName;
    if (data.equalsIgnoreCase("empty")) {
      docName = "empty-list";
      pagedList = new BeerPagedList();
      snippet = responseFields(fieldWithPath("content").description("All list of beer details"));
    } else if (data.startsWith("paged")) {
      if (data.equalsIgnoreCase("paged")) {
        pagedList = new BeerPagedList(List.of(beer), PageRequest.of(1, 1), 1);
      } else {
        pagedList = new BeerPagedList(List.of(beer), 1, 10, 1L, null, false, 1, null, true, 1);
      }
      snippet = responseFields(responseArrayFields()).andWithPrefix("",
          fieldWithPath("pageable.sort.sorted").description("Whether the results are sorted"),
          fieldWithPath("pageable.sort.unsorted").description("Whether the results are unsorted"),
          fieldWithPath("pageable.sort.empty").description("Whether the sort is empty"),
          fieldWithPath("pageable.pageNumber").description("The current page number"),
          fieldWithPath("pageable.pageSize").description("The number of items per page"),
          fieldWithPath("pageable.offset").description("The offset of the first item in the page"),
          fieldWithPath("pageable.paged").description("Whether the results are paged"),
          fieldWithPath("pageable.unpaged").description("Whether the results are unpaged"));
      docName = "paged-list";
    } else {
      pagedList = new BeerPagedList(List.of(beer));
      snippet = responseFields(responseArrayFields());
      docName = "beer-list";
    }
    when(beerService.listBeers(anyString(), anyString(), any(PageRequest.class),
        anyBoolean())).thenReturn(pagedList);
    mockMvc.perform(get("/api/v1/beer").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document(docName, snippet.andWithPrefix("", pageableFields())));
  }

  @Test
  void getBeerById() throws Exception {
    when(beerService.getById(any(UUID.class), anyBoolean())).thenReturn(beer);
    mockMvc.perform(get("/api/v1/beer/{beerId}", UUID.randomUUID()).param("isCold", "yes")
        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(
        document("get-beer-by-id",
            pathParameters(parameterWithName("beerId").description("UUID of desired beer to get.")),
            queryParameters(parameterWithName("isCold").description("Is Beer Cold Query Param.")),
            buildResponseSnippet()));
  }

  @Test
  void getBeerByUpc() throws Exception {
    when(beerService.getByUpc(anyString())).thenReturn(beer);
    mockMvc.perform(get("/api/v1/beer/upc/{upc}", "0002232311").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andDo(document("get-beer-by-upc",
            pathParameters(parameterWithName("upc").description("UPC of desired beer to get.")),
            buildResponseSnippet()));
  }

  @Test
  void saveNewBeer() throws Exception {
    when(beerService.saveNewBeer(any(BeerDTO.class))).thenReturn(beer);
    mockMvc.perform(post("/api/v1/beer/").contentType(MediaType.APPLICATION_JSON).content(beerJson))
        .andExpect(status().isCreated())
        .andDo(document("save-new-beer", buildRequestFieldSnippet(), buildResponseSnippet()));
  }

  @Test
  void updateBeer() throws Exception {
    when(beerService.updateBeer(any(UUID.class), any(BeerDTO.class))).thenReturn(beer);
    mockMvc.perform(
        put("/api/v1/beer/{beerId}", UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON)
            .content(beerJson)).andExpect(status().isNoContent()).andDo(document("update-beer",
        pathParameters(parameterWithName("beerId").description("UUID of desired beer to get.")),
        buildRequestFieldSnippet()));
  }

  private RequestFieldsSnippet buildRequestFieldSnippet() {
    return requestFields(fields.withPath("id").optional().ignored(),
        fields.withPath("version").optional().ignored(),
        fields.withPath("createdDate").optional().ignored(),
        fields.withPath("lastModifiedDate").optional().ignored(),
        fields.withPath("beerName").description("Beer Name"),
        fields.withPath("beerStyle").description("Beer Style"),
        fields.withPath("upc").description("UPC of Beer").attributes(),
        fields.withPath("price").description("Price"),
        fields.withPath("quantityOnHand").type(NUMBER).description("Quantity On Hand").optional());
  }

  private ResponseFieldsSnippet buildResponseSnippet() {
    return responseFields(fieldWithPath("id").description("Id of Beer"),
        fieldWithPath("version").description("Version number"),
        fieldWithPath("createdDate").description("Date Created"),
        fieldWithPath("lastModifiedDate").description("Date Updated"),
        fieldWithPath("beerName").description("Beer Name"),
        fieldWithPath("beerStyle").description("Beer Style"),
        fieldWithPath("upc").description("UPC of Beer"),
        fieldWithPath("price").description("Price"),
        fieldWithPath("quantityOnHand").description("Quantity On hand"));
  }

  private FieldDescriptor[] responseArrayFields() {
    return new FieldDescriptor[]{fieldWithPath("content").description("All list of beer details"),
        fieldWithPath("content[].id").description("Id of Beer").optional(),
        fieldWithPath("content[].version").description("Version number").optional(),
        fieldWithPath("content[].createdDate").description("Date Created").optional(),
        fieldWithPath("content[].lastModifiedDate").description("Date Updated").optional(),
        fieldWithPath("content[].beerName").description("Beer Name").optional(),
        fieldWithPath("content[].beerStyle").description("Beer Style").optional(),
        fieldWithPath("content[].upc").description("UPC of Beer").optional(),
        fieldWithPath("content[].price").description("Price").optional(),
        fieldWithPath("content[].quantityOnHand").description("Quantity On hand").optional()};
  }

  private FieldDescriptor[] pageableFields() {
    return new FieldDescriptor[]{fieldWithPath("pageable").description("Pagination instance"),
        fieldWithPath("totalPages").description("The total number of pages"),
        fieldWithPath("totalElements").description("The total number of elements"),
        fieldWithPath("last").description("Whether this is the last page"),
        fieldWithPath("first").description("Whether this is the first page"),
        fieldWithPath("sort.sorted").description("Whether the results are sorted"),
        fieldWithPath("sort.unsorted").description("Whether the results are unsorted"),
        fieldWithPath("sort.empty").description("Whether the sort is empty"),
        fieldWithPath("numberOfElements").description("The number of elements in the current page"),
        fieldWithPath("size").description("The size of the page"),
        fieldWithPath("number").description("The current page number"),
        fieldWithPath("empty").description("Whether the current page is empty")};
  }

  private static final class ConstrainedFields {

    private final ConstraintDescriptions constraintDescriptions;

    ConstrainedFields(Class<?> input) {
      this.constraintDescriptions = new ConstraintDescriptions(input);
    }

    private FieldDescriptor withPath(String path) {
      String fields = String.join(". ", this.constraintDescriptions.descriptionsForProperty(path));
      return fieldWithPath(path).attributes(key("constraints").value(fields));
    }
  }
}