package com.rte_france.plasma.hackathon.listener;

import com.rte_france.plasma.hackathon.rest.MeterReadingController;
import org.opensmartgridplatform.adapter.kafka.MeterReading;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
public class MeterReadingListener {

    @Autowired
    private MeterReadingController controller;

    @KafkaListener(topics = "${topics.meter-reading-power}",groupId = "${spring.kafka.consumer.group-id}" )
    public void readMeterReadingPower(MeterReading meterReading){
        System.out.println("meter-reading-power : " + meterReading);
        SseEmitter latestEm = controller.getLatestEmitter();
        try {
            if(latestEm != null){
                latestEm.send(meterReading.toString());
            }
        } catch (IOException e) {
            latestEm.completeWithError(e);
        }
    }

    @KafkaListener(topics = "${topics.meter-reading-internal-temp}",groupId = "${spring.kafka.consumer.group-id}" )
    public void readmeterReadingInternalTemp(MeterReading meterReading){
        System.out.println("meter-reading-internal-temp : " + meterReading);
        SseEmitter latestEm = controller.getLatestEmitter();
        try {
            if(latestEm != null){
                latestEm.send(meterReading.toString());
            }
        } catch (IOException e) {
            latestEm.completeWithError(e);
        }
    }
}
