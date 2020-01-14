package com.rte_france.plasma.hackathon.mappers;

import org.mapstruct.*;
import org.opensmartgridplatform.adapter.kafka.MeterReading;
import org.opensmartgridplatform.adapter.kafka.MeterReadingEnhanced;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface MeterReadingMapper {


    MeterReadingEnhanced beanToDto(MeterReading meterReading);
    @AfterMapping
    default void addBackReference(@MappingTarget MeterReadingEnhanced target) {
        target.setExternalTemperature("15");
        if(!target.getIntervalBlocks().isEmpty() && target.getIntervalBlocks().size() == 2){
            if(Float.parseFloat(target.getIntervalBlocks().get(1).getIntervalReadings().get(0).getValue()) >= 230){
                target.setAlert("Red");
            }else if(Float.parseFloat(target.getIntervalBlocks().get(1).getIntervalReadings().get(0).getValue()) >= 200 && Integer.parseInt(target.getIntervalBlocks().get(1).getIntervalReadings().get(0).getValue()) < 230){
                target.setAlert("Orange");
            }else if(Float.parseFloat(target.getIntervalBlocks().get(1).getIntervalReadings().get(0).getValue()) < 200){
                target.setAlert("Green");
            }
        }
    }
}
