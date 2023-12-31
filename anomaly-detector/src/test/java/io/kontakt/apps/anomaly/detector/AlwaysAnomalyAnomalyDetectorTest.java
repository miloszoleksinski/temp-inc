package io.kontakt.apps.anomaly.detector;

import io.kontakt.apps.event.Anomaly;
import io.kontakt.apps.event.TemperatureReading;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.time.Instant;

@TestPropertySource(locations = "classpath:application-test-algorithm-always.properties")
public class AlwaysAnomalyAnomalyDetectorTest extends AbstractIntegrationTest {

    @Value("${spring.cloud.stream.bindings.anomalyDetectorProcessor-in-0.destination}")
    private String inputTopic;

    @Value("${spring.cloud.stream.bindings.anomalyDetectorProcessor-out-0.destination}")
    private String outputTopic;

    @Test
    void testInOutFlow() {
        try (TestKafkaConsumer<Anomaly> consumer = new TestKafkaConsumer<>(kafkaContainer.getBootstrapServers(), outputTopic, Anomaly.class);
             TestKafkaProducer<TemperatureReading> producer = new TestKafkaProducer<>(kafkaContainer.getBootstrapServers(), inputTopic)) {
            TemperatureReading temperatureReading = new TemperatureReading(20d, "room", "thermometer", Instant.parse("2023-01-01T00:00:00.000Z"));
            producer.produce(temperatureReading.thermometerId(), temperatureReading);
            consumer.drain(
                    consumerRecords -> consumerRecords.stream().anyMatch(r -> r.value().thermometerId().equals(temperatureReading.thermometerId())),
                    Duration.ofSeconds(5)
            );
        }
    }
}
