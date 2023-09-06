package io.kontakt.apps.temperature.generator;

import io.kontakt.apps.event.TemperatureReading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class TemperatureStreamPublisher {
    Logger logger = LoggerFactory.getLogger(TemperatureStreamPublisher.class);
    private final Sinks.Many<Message<TemperatureReading>> messageProducer = Sinks.many().multicast().onBackpressureBuffer();

    public Flux<Message<TemperatureReading>> getMessageProducer() {
        return messageProducer.asFlux();
    }

    public void publish(TemperatureReading temperatureReading) {
        logger.info("Sending temperature measurement: {}", temperatureReading.temperature());
        messageProducer.tryEmitNext(
                MessageBuilder.withPayload(temperatureReading)
                        .setHeader("identifier", temperatureReading.thermometerId())
                        .build()
        );
    }
}
