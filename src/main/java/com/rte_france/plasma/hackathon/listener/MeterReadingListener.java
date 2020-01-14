package com.rte_france.plasma.hackathon.listener;

import com.rte_france.plasma.hackathon.rest.MeterReadingController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
public class MeterReadingListener {

    @Autowired
    private MeterReadingController controller;

    @org.springframework.kafka.annotation.KafkaListener(topics = "${spring.kafka.consumer.topics}",groupId = "${spring.kafka.consumer.group-id}" )
    public void read(String meterReading){
        SseEmitter latestEm = controller.getLatestEmitter();
        try {
            if(latestEm != null){
                latestEm.send(meterReading);
            }
        } catch (IOException e) {
            latestEm.completeWithError(e);
        }
    }


}
