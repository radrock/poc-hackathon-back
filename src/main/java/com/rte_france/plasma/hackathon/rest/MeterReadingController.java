package com.rte_france.plasma.hackathon.rest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@RestController("MeterReadingController.v1")
@RequestMapping("meter-reading")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MeterReadingController {

    private final List<SseEmitter> emitters = new ArrayList<>();

    @GetMapping("/read-messages")
    public SseEmitter getKafkaMessages() {

        SseEmitter emitter = new SseEmitter( 1 * 60 * 1000L );
        emitters.add(emitter);

        emitter.onCompletion(new Runnable() {
            @Override
            public void run() {
                emitters.remove(emitter);
            }
        });

        emitter.onTimeout(new Runnable() {
            @Override
            public void run() {
                emitters.remove(emitter);
            }
        });

        return emitter;
    }

    public List<SseEmitter> getEmitters() {
        return emitters;
    }

    public SseEmitter getLatestEmitter() {
        return (emitters.isEmpty()) ? null : emitters.get(emitters.size()-1);
    }
}
