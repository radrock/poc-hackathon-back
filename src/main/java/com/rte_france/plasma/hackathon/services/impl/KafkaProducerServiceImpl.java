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
import java.util.Random;

@Component
public class KafkaProducerServiceImpl implements KafkaProducerService {
    @Autowired
    private MeterReadingMapper mapper;
    @Autowired
    private  KafkaTemplate<String, MeterReadingEnhanced> kafkaTemplate;

    @Value("${topics.meter-reading-power-it}")
    private  String topicName;

    @Value("${temperatures.thresholds.normal}")
    private  String normalTemperature;

    @Value("${temperatures.thresholds.ubnormal}")
    private  String ubnormalTemperature;

    @Autowired
    private MeterReadingController controller;

    @Override
    public void sendToKafka(MeterReading meterReading) {
        MeterReadingEnhanced enhanced = mapper.beanToDto(meterReading);
        enhanced.setExternalTemperature(String.valueOf(externalTemperature()));
        applaySimpleRUles(enhanced);
        kafkaTemplate.send(topicName,enhanced);
        kafkaTemplate.flush();
        sendToSee(enhanced);
    }


    private void applaySimpleRUles(MeterReadingEnhanced enhanced){
        if(!enhanced.getIntervalBlocks().isEmpty() && enhanced.getIntervalBlocks().size() == 2){
            if(Float.parseFloat(enhanced.getIntervalBlocks().get(1).getIntervalReadings().get(0).getValue()) >= Float.parseFloat(ubnormalTemperature)){
                enhanced.setAlert("Red");
            }else if(Float.parseFloat(enhanced.getIntervalBlocks().get(1).getIntervalReadings().get(0).getValue()) >= Float.parseFloat(normalTemperature) && Float.parseFloat(enhanced.getIntervalBlocks().get(1).getIntervalReadings().get(0).getValue()) < Float.parseFloat(ubnormalTemperature)){
                enhanced.setAlert("Orange");
            }else if(Float.parseFloat(enhanced.getIntervalBlocks().get(1).getIntervalReadings().get(0).getValue()) < Float.parseFloat(normalTemperature)){
                enhanced.setAlert("Green");
            }
        }
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
    private float externalTemperature() {
        float leftLimit = Float.valueOf("15");
        float rightLimit = Float.valueOf("16");
        return leftLimit + new Random().nextFloat() * (rightLimit - leftLimit);
    }
}
