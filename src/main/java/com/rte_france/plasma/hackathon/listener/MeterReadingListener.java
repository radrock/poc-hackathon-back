package com.rte_france.plasma.hackathon.listener;

import com.rte_france.plasma.hackathon.rest.MeterReadingController;
import com.rte_france.plasma.hackathon.services.KafkaProducerService;
import org.opensmartgridplatform.adapter.kafka.MeterReading;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MeterReadingListener {


    @Autowired
    private KafkaProducerService service;
    private List<MeterReading> temperatures = new ArrayList<>();

    @KafkaListener(topics = "${topics.meter-reading-power}",groupId = "${spring.kafka.consumer.group-id}" )
    public void readMeterReadingPower(MeterReading meterReading){
        Optional<MeterReading> power =  temperatures.stream()
                .filter(tmp -> tmp.getUsagePoint().getMRid().equals(meterReading.getUsagePoint().getMRid()) && tmp.getValuesInterval().getStart() <= meterReading.getValuesInterval().getStart())
                .findFirst();
        if(power.isPresent()){
            meterReading.getIntervalBlocks().add(power.get().getIntervalBlocks().get(0));
            this.temperatures.remove(power.get());
        }
        service.sendToKafka(meterReading);

    }

    @KafkaListener(topics = "${topics.meter-reading-internal-temp}",groupId = "${spring.kafka.consumer.group-id}" )
    public void readMeterReadingInternalTemp(MeterReading meterReading){
        temperatures.add(meterReading);
    }
}
