package com.rte_france.plasma.hackathon.services.impl;

import com.rte_france.plasma.hackathon.mappers.MeterReadingMapper;
import com.rte_france.plasma.hackathon.rest.MeterReadingController;
import com.rte_france.plasma.hackathon.services.KafkaProducerService;
import org.opensmartgridplatform.adapter.kafka.MeterReading;
import org.opensmartgridplatform.adapter.kafka.MeterReadingEnhanced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
public class KafkaProducerServiceImpl implements KafkaProducerService {
    @Autowired
    private MeterReadingMapper mapper;
    @Autowired
    private  KafkaTemplate<String, MeterReadingEnhanced> kafkaTemplate;

    @Value("${topics.meter-reading-power-it}")
    private  String topicName;

    @Autowired
    private MeterReadingController controller;

    @Override
    public void sendToKafka(MeterReading meterReading) {
        MeterReadingEnhanced enchancedMeterReading = mapper.beanToDto(meterReading);
        kafkaTemplate.send(topicName,enchancedMeterReading);
        kafkaTemplate.flush();
        sendToSee(enchancedMeterReading);
    }


    private void sendToSee(MeterReadingEnhanced meterReading) {
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
