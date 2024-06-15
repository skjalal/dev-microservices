package com.example.beerorderservice.web.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.beerorderservice.services.BeerOrderService;
import com.example.brewery.models.BeerOrderDTO;
import com.example.brewery.models.BeerOrderLineDTO;
import com.example.brewery.models.BeerOrderPagedList;
import com.example.brewery.models.OrderStatusUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {BeerOrderController.class})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.beer.com", uriPort = 80)
class BeerOrderControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  BeerOrderService beerOrderService;

  BeerOrderDTO beerOrderDTO;
  UUID id;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    OffsetDateTime dateTime = OffsetDateTime.now();
    BeerOrderLineDTO orderLineDTO = BeerOrderLineDTO.builder().orderQuantity(2).upc("00088272")
        .beerId(id).beerName("ALA").beerStyle("ALA").price(BigDecimal.valueOf(12.989)).id(id)
        .version(1).createdDate(dateTime).lastModifiedDate(dateTime).build();
    beerOrderDTO = BeerOrderDTO.builder().id(id).version(1).orderStatus("NEW").customerId(id)
        .customerRef(id.toString()).orderStatusCallbackUrl("/re-order")
        .beerOrderLines(List.of(orderLineDTO)).createdDate(dateTime).lastModifiedDate(dateTime)
        .build();
    OrderStatusUpdate.builder().id(id).orderId(id).orderStatus("NEW").version(1)
        .customerRef(id.toString()).createdDate(dateTime).lastModifiedDate(dateTime).build();
  }

  @Test
  void listOrders() throws Exception {
    BeerOrderPagedList list = new BeerOrderPagedList(List.of(beerOrderDTO), Pageable.ofSize(1), 1);
    when(beerOrderService.listOrders(any(UUID.class), any(Pageable.class))).thenReturn(list);
    mockMvc.perform(get("/api/v1/customers/{customerId}/orders", id).queryParam("pageNumber", "0")
            .queryParam("pageSize", "10").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andDo(document("get-beer-orders", pathParameters(
                parameterWithName("customerId").description("UUID of desired customerId.")),
            queryParameters(parameterWithName("pageNumber").description("Page number for list"),
                parameterWithName("pageSize").description("Total number of pages.")),
            responseFields(fieldWithPath("content").description("All list of orders details"),
                fieldWithPath("content[].id").description("ID of the order"),
                fieldWithPath("content[].version").description("Record version"),
                fieldWithPath("content[].orderStatus").description("Status of order"),
                fieldWithPath("content[].createdDate").description(
                    "Creation date of order details"),
                fieldWithPath("content[].lastModifiedDate").description(
                    "Last modified date of order details"),
                fieldWithPath("content[].customerId").description(
                    "ID of the customer of the order"),
                fieldWithPath("content[].customerRef").description("Customer reference number"),
                fieldWithPath("content[].orderStatusCallbackUrl").description("Order callback"),
                fieldWithPath("content[].beerOrderLines").description("Number of lines of order"),
                fieldWithPath("content[].beerOrderLines[].id").description("ID of the order line"),
                fieldWithPath("content[].beerOrderLines[].beerId").description(
                    "ID of the beer ordered"),
                fieldWithPath("content[].beerOrderLines[].beerName").description(
                    "Name of the beer ordered"),
                fieldWithPath("content[].beerOrderLines[].beerStyle").description(
                    "Style of the beer ordered"),
                fieldWithPath("content[].beerOrderLines[].upc").description(
                    "Upc of the beer ordered"),
                fieldWithPath("content[].beerOrderLines[].price").description(
                    "Price of the beer ordered"),
                fieldWithPath("content[].beerOrderLines[].orderQuantity").description(
                    "Quantity of the beer ordered"),
                fieldWithPath("content[].beerOrderLines[].quantityAllocated").description(
                    "Quantity allocated of the beer ordered"),
                fieldWithPath("content[].beerOrderLines[].version").description("Record version"),
                fieldWithPath("content[].beerOrderLines[].createdDate").description(
                    "Creation date of order line details"),
                fieldWithPath("content[].beerOrderLines[].lastModifiedDate").description(
                    "Last modified date of order line details"),
                fieldWithPath("pageable").description("Pagination instance"),
                fieldWithPath("pageable.sort.sorted").description("Whether the results are sorted"),
                fieldWithPath("pageable.sort.unsorted").description(
                    "Whether the results are unsorted"),
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

  @Test
  void placeOrder() throws Exception {
    when(beerOrderService.placeOrder(any(UUID.class), any(BeerOrderDTO.class))).thenReturn(
        beerOrderDTO);
    mockMvc.perform(post("/api/v1/customers/{customerId}/orders", id).content(
            objectMapper.writeValueAsBytes(beerOrderDTO)).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andDo(
        document("place-order", pathParameters(
                parameterWithName("customerId").description("UUID of desired customerId.")),
            requestFields(fieldValues()), responseFields(fieldValues())));
  }

  @Test
  void getOrder() throws Exception {
    when(beerOrderService.getOrderById(any(UUID.class), any(UUID.class))).thenReturn(beerOrderDTO);
    mockMvc.perform(get("/api/v1/customers/{customerId}/orders/{orderId}", id, id).accept(
        MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(document("get-order",
        pathParameters(parameterWithName("customerId").description("UUID of desired customer."),
            parameterWithName("orderId").description("UUID of desired order.")),
        responseFields(fieldValues())));
  }

  @Test
  void pickupOrder() throws Exception {
    doNothing().when(beerOrderService).pickupOrder(any(UUID.class), any(UUID.class));
    mockMvc.perform(put("/api/v1/customers/{customerId}/orders/{orderId}/pickup", id, id))
        .andExpect(status().isNoContent()).andDo(document("pickup-order",
            pathParameters(parameterWithName("customerId").description("UUID of desired customer."),
                parameterWithName("orderId").description("UUID of desired order."))));
  }

  private FieldDescriptor[] fieldValues() {
    return new FieldDescriptor[]{fieldWithPath("id").description("ID of the order"),
        fieldWithPath("version").description("Record version"),
        fieldWithPath("orderStatus").description("Status of order"),
        fieldWithPath("createdDate").description("Creation date of order details"),
        fieldWithPath("lastModifiedDate").description("Last modified date of order details"),
        fieldWithPath("customerId").description("ID of the customer of the order"),
        fieldWithPath("customerRef").description("Customer reference number"),
        fieldWithPath("orderStatusCallbackUrl").description("Order callback"),
        fieldWithPath("beerOrderLines").description("Number of lines of order"),
        fieldWithPath("beerOrderLines[].id").description("ID of the order line"),
        fieldWithPath("beerOrderLines[].beerId").description("ID of the beer ordered"),
        fieldWithPath("beerOrderLines[].beerName").description("Name of the beer ordered"),
        fieldWithPath("beerOrderLines[].beerStyle").description("Style of the beer ordered"),
        fieldWithPath("beerOrderLines[].upc").description("Upc of the beer ordered"),
        fieldWithPath("beerOrderLines[].price").description("Price of the beer ordered"),
        fieldWithPath("beerOrderLines[].orderQuantity").description("Quantity of the beer ordered"),
        fieldWithPath("beerOrderLines[].quantityAllocated").description(
            "Quantity allocated of the beer ordered"),
        fieldWithPath("beerOrderLines[].version").description("Record version"),
        fieldWithPath("beerOrderLines[].createdDate").description(
            "Creation date of order line details"),
        fieldWithPath("beerOrderLines[].lastModifiedDate").description(
            "Last modified date of order line details")};
  }
}