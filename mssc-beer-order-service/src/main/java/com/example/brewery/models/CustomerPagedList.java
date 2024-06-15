package com.example.brewery.models;

import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class CustomerPagedList extends PageImpl<CustomerDTO> {

  public CustomerPagedList(List<CustomerDTO> content, Pageable pageable, long total) {
    super(content, pageable, total);
  }

}
