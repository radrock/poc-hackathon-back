package com.rte_france.plasma.hackathon.mappers;

import org.mapstruct.*;
import org.opensmartgridplatform.adapter.kafka.MeterReading;
import org.opensmartgridplatform.adapter.kafka.MeterReadingEnhanced;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface MeterReadingMapper {


    MeterReadingEnhanced beanToDto(MeterReading meterReading);
}
