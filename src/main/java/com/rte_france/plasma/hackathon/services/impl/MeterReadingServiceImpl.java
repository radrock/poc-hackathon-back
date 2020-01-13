package com.rte_france.plasma.hackathon.services.impl;

import com.rte_france.plasma.hackathon.rest.MeterReadingController;
import com.rte_france.plasma.hackathon.services.MeterReadingService;
import org.opensmartgridplatform.adapter.kafka.MeterReading;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
public class MeterReadingServiceImpl implements MeterReadingService {

    @Autowired
    private MeterReadingController controller;

    @Override
    @KafkaListener(topics = "${spring.kafka.consumer.topics}",groupId = "${spring.kafka.consumer.group-id}" )
    public void read(MeterReading meterReading){
        System.out.println(meterReading);
        SseEmitter latestEm = controller.getLatestEmitter();
        try {
            latestEm.send(meterReading);
        } catch (IOException e) {
            latestEm.completeWithError(e);
        }
    }
}
