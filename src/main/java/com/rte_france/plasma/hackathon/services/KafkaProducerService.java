package com.rte_france.plasma.hackathon.services;

import org.opensmartgridplatform.adapter.kafka.MeterReading;

public interface KafkaProducerService {

    void sendToKafka(MeterReading meterReading);

}
