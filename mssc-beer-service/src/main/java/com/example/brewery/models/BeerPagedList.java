package com.example.brewery.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class BeerPagedList extends PageImpl<BeerDTO> {

  @Serial
  private static final long serialVersionUID = 235252322521L;

  public BeerPagedList() {
    super(new ArrayList<>());
  }

  public BeerPagedList(List<BeerDTO> content) {
    super(content);
  }

  public BeerPagedList(List<BeerDTO> content, Pageable pageable, long total) {
    super(content, pageable, total);
  }

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public BeerPagedList(@JsonProperty("content") List<BeerDTO> content,
      @JsonProperty("number") int number, @JsonProperty("size") int size,
      @JsonProperty("totalElements") Long totalElements,
      @JsonProperty("pageable") JsonNode pageable, @JsonProperty("last") boolean last,
      @JsonProperty("totalPages") int totalPages, @JsonProperty("sort") JsonNode sort,
      @JsonProperty("first") boolean first,
      @JsonProperty("numberOfElements") int numberOfElements) {

    this(content, PageRequest.of(number, size), totalElements);
  }
}
