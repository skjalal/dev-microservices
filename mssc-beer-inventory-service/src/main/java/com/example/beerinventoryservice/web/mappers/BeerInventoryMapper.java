package com.example.beerinventoryservice.web.mappers;

import com.example.beerinventoryservice.domain.BeerInventory;
import com.example.brewery.models.BeerInventoryDTO;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;

@Mapper
public interface BeerInventoryMapper {

  BeerInventory beerInventoryDtoToBeerInventory(BeerInventoryDTO beerInventoryDTO);

  BeerInventoryDTO beerInventoryToBeerInventoryDto(BeerInventory beerInventory);

  default OffsetDateTime asOffsetDateTime(Timestamp ts) {
    if (ts != null) {
      return OffsetDateTime.of(ts.toLocalDateTime().getYear(), ts.toLocalDateTime().getMonthValue(),
          ts.toLocalDateTime().getDayOfMonth(), ts.toLocalDateTime().getHour(),
          ts.toLocalDateTime().getMinute(), ts.toLocalDateTime().getSecond(),
          ts.toLocalDateTime().getNano(), ZoneOffset.UTC);
    } else {
      return null;
    }
  }

  default Timestamp asTimestamp(OffsetDateTime offsetDateTime) {
    if (offsetDateTime != null) {
      return Timestamp.valueOf(offsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
    } else {
      return null;
    }
  }
}
