package com.rte_france.plasma.hackathon.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.avro.AvroFactory;
import com.fasterxml.jackson.dataformat.avro.AvroSchema;
import com.fasterxml.jackson.dataformat.avro.schema.AvroSchemaGenerator;
import com.rte_france.plasma.hackathon.rest.MeterReadingController;
import com.rte_france.plasma.material.referential.TechnicalStationAvro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
public class MeterReadingListener {

    @Autowired
    private MeterReadingController controller;

    @org.springframework.kafka.annotation.KafkaListener(topics = "${spring.kafka.consumer.topics}",groupId = "${spring.kafka.consumer.group-id}" )
    public void read(TechnicalStationAvro meterReading){
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
