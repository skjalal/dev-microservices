package com.example.beerorderservice.web.mappers;

import com.example.beerorderservice.domain.Customer;
import com.example.brewery.models.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper extends DateMapper {

  CustomerDTO customerToDto(Customer customer);

  Customer dtoToCustomer(CustomerDTO dto);
}
