package com.example.beerorderservice.web.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.beerorderservice.services.CustomerService;
import com.example.brewery.models.CustomerDTO;
import com.example.brewery.models.CustomerPagedList;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {CustomerController.class})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.beer.com", uriPort = 80)
class CustomerControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  CustomerService customerService;

  CustomerPagedList list;

  @BeforeEach
  void setUp() {
    OffsetDateTime dateTime = OffsetDateTime.now();
    CustomerDTO customerDTO = CustomerDTO.builder().id(UUID.randomUUID()).version(1).name("Test")
        .createdDate(dateTime).lastModifiedDate(dateTime).build();
    list = new CustomerPagedList(List.of(customerDTO), Pageable.ofSize(1), 1);
  }

  @Test
  void testListCustomers() throws Exception {
    when(customerService.listCustomers(any(Pageable.class))).thenReturn(list);
    mockMvc.perform(get("/api/v1/customers").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andDo(document("get-customers",
            responseFields(fieldWithPath("content").description("All list of customer details"),
                fieldWithPath("content[].id").description("ID of the customer"),
                fieldWithPath("content[].version").description("Record version"),
                fieldWithPath("content[].name").description("Name of customer"),
                fieldWithPath("content[].createdDate").description("Creation date of customer details"),
                fieldWithPath("content[].lastModifiedDate").description(
                    "Last modified date of customer details"),
                fieldWithPath("pageable").description("Pagination instance"),
                fieldWithPath("pageable.sort.sorted").description("Whether the results are sorted"),
                fieldWithPath("pageable.sort.unsorted").description("Whether the results are unsorted"),
                fieldWithPath("pageable.sort.empty").description("Whether the sort is empty"),
                fieldWithPath("pageable.pageNumber").description("The current page number"),
                fieldWithPath("pageable.pageSize").description("The number of items per page"),
                fieldWithPath("pageable.offset").description(
                    "The offset of the first item in the page"),
                fieldWithPath("pageable.paged").description("Whether the results are paged"),
                fieldWithPath("pageable.unpaged").description("Whether the results are unpaged"),
                fieldWithPath("totalPages").description("The total number of pages"),
                fieldWithPath("totalElements").description("The total number of elements"),
                fieldWithPath("last").description("Whether this is the last page"),
                fieldWithPath("first").description("Whether this is the first page"),
                fieldWithPath("sort.sorted").description("Whether the results are sorted"),
                fieldWithPath("sort.unsorted").description("Whether the results are unsorted"),
                fieldWithPath("sort.empty").description("Whether the sort is empty"),
                fieldWithPath("numberOfElements").description(
                    "The number of elements in the current page"),
                fieldWithPath("size").description("The size of the page"),
                fieldWithPath("number").description("The current page number"),
                fieldWithPath("empty").description("Whether the current page is empty"))));
  }
}