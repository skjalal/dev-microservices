package com.example.msscbeerservice.web.mappers;

import com.example.msscbeerservice.domain.Beer;
import com.example.brewery.models.BeerDTO;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper
@DecoratedWith(value = BeerMapperDecorator.class)
public interface BeerMapper {

  BeerDTO beerToBeerDTO(Beer beer);

  Beer beerDTOToBeer(BeerDTO beerDTO);

  BeerDTO beerToBeerDTOWithInventory(Beer beer);

  default OffsetDateTime asOffsetDateTime(Timestamp ts) {
    if (ts == null) {
      return null;
    }

    var ldt = ts.toLocalDateTime();
    return OffsetDateTime.of(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth(), ldt.getHour(),
        ldt.getMinute(), ldt.getSecond(), ldt.getNano(), ZoneOffset.UTC);
  }

  default Timestamp asTimestamp(OffsetDateTime offsetDateTime) {
    if (offsetDateTime == null) {
      return null;
    }

    return Timestamp.valueOf(offsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
  }
}
