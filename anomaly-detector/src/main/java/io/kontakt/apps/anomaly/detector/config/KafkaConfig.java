package io.kontakt.apps.anomaly.detector.config;

import io.kontakt.apps.anomaly.detector.TemperatureMeasurementsListener;
import io.kontakt.apps.event.Anomaly;
import io.kontakt.apps.event.TemperatureReading;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class KafkaConfig {

    @Bean
    public Function<KStream<String, TemperatureReading>, KStream<String, Anomaly>> anomalyDetectorProcessor(
            final TemperatureMeasurementsListener temperatureMeasurementsListener) {
        return temperatureMeasurementsListener;
    }

}
