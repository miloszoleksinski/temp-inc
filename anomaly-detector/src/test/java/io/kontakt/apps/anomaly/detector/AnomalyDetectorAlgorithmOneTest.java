package io.kontakt.apps.anomaly.detector;

import io.kontakt.apps.event.Anomaly;
import io.kontakt.apps.event.TemperatureReading;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource(locations = "classpath:application-test-algorithm-one.properties")
public class AnomalyDetectorAlgorithmOneTest extends AbstractIntegrationTest {

    @Value("${spring.cloud.stream.bindings.anomalyDetectorProcessor-in-0.destination}")
    private String inputTopicWithAnomaly;

    @Value("${spring.cloud.stream.bindings.anomalyDetectorProcessor-out-0.destination}")
    private String outputTopicWithAnomaly;

    @Value("${spring.cloud.stream.bindings.anomalyDetectorProcessor-in-1.destination}")
    private String inputTopicWithoutAnomaly;

    @Value("${spring.cloud.stream.bindings.anomalyDetectorProcessor-out-1.destination}")
    private String outputTopicWithoutAnomaly;

    @Test
    void testAlgorithmOneWithAnomaly() {
        try (TestKafkaConsumer<Anomaly> consumer = new TestKafkaConsumer<>(kafkaContainer.getBootstrapServers(), outputTopicWithAnomaly, Anomaly.class);
             TestKafkaProducer<TemperatureReading> producer = new TestKafkaProducer<>(kafkaContainer.getBootstrapServers(), inputTopicWithAnomaly)) {
            sendTenReadingsWithOneAnomaly(producer);
            consumer.drain(
                    consumerRecords -> consumerRecords.stream().anyMatch(r -> r.value().thermometerId().equals("thermometer")),
                    Duration.ofSeconds(5)
            );
        }
    }

    @Test
    void testAlgorithmOneWithoutAnomaly() {
        try (TestKafkaConsumer<Anomaly> consumer = new TestKafkaConsumer<>(kafkaContainer.getBootstrapServers(), outputTopicWithoutAnomaly, Anomaly.class);
             TestKafkaProducer<TemperatureReading> producer = new TestKafkaProducer<>(kafkaContainer.getBootstrapServers(), inputTopicWithoutAnomaly)) {
            sendTenReadingsWithoutAnomaly(producer);
            consumer.assertNoRecords(Duration.ofSeconds(5));
        }
    }

    private void sendTenReadingsWithOneAnomaly(final TestKafkaProducer<TemperatureReading> producer) {
        for(int i=0; i<9; i++) {
            TemperatureReading temperatureReadingTwo = new TemperatureReading(20d, "room", "thermometer", Instant.now());
            producer.produce(temperatureReadingTwo.thermometerId(), temperatureReadingTwo);
        }
        TemperatureReading temperatureReading = new TemperatureReading(26d, "room", "thermometer", Instant.now());
        producer.produce(temperatureReading.thermometerId(), temperatureReading);
    }

    private void sendTenReadingsWithoutAnomaly(final TestKafkaProducer<TemperatureReading> producer) {
        for(int i=0; i<10; i++) {
            TemperatureReading temperatureReadingTwo = new TemperatureReading(20d, "room", "thermometer", Instant.now());
            producer.produce(temperatureReadingTwo.thermometerId(), temperatureReadingTwo);
        }
    }

    @Test
    public void testNoAnomalyDetected() {
        AnomalyDetectorAlgorithmOne algorithmOne = new AnomalyDetectorAlgorithmOne();

        List<TemperatureReading> readings = new ArrayList<>();
        for (double temperature : new double[]{20.1, 21.2, 20.3, 19.1, 20.1, 19.2, 20.1, 18.1, 19.4, 20.1, 23.1, 22.1}) {
            readings.add(new TemperatureReading(temperature, "roomId", "thermometerId", Instant.now()));
        }

        Optional<Anomaly> anomaly = algorithmOne.apply(readings);
        assertFalse(anomaly.isPresent(), "Anomaly should not be detected");
    }

    @Test
    public void testAnomalyDetected() {
        AnomalyDetectorAlgorithmOne algorithmOne = new AnomalyDetectorAlgorithmOne();

        List<TemperatureReading> readings = new ArrayList<>();
        for (double temperature : new double[]{20.1, 21.2, 20.3, 19.1, 20.1, 19.2, 20.1, 18.1, 19.4, 20.1, 27.1, 23.1}) {
            readings.add(new TemperatureReading(temperature, "roomId", "thermometerId", Instant.now()));
        }

        Optional<Anomaly> anomaly = algorithmOne.apply(readings);
        assertTrue(anomaly.isPresent(), "Anomaly should be detected");
    }

}
