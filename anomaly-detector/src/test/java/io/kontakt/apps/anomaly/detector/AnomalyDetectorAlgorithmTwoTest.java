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

@TestPropertySource(locations = "classpath:application-test-algorithm-two.properties")
public class AnomalyDetectorAlgorithmTwoTest extends AbstractIntegrationTest {

    @Value("${spring.cloud.stream.bindings.anomalyDetectorProcessor-in-0.destination}")
    private String inputTopicWithAnomaly;

    @Value("${spring.cloud.stream.bindings.anomalyDetectorProcessor-out-0.destination}")
    private String outputTopicWithAnomaly;

    @Value("${spring.cloud.stream.bindings.anomalyDetectorProcessor-in-1.destination}")
    private String inputTopicWithoutAnomaly;

    @Value("${spring.cloud.stream.bindings.anomalyDetectorProcessor-out-1.destination}")
    private String outputTopicWithoutAnomaly;
    @Test
    void testAlgorithmTwoWithAnomaly() {
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
    void testAlgorithmTwoWithoutAnomaly() {
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
        AnomalyDetectorAlgorithmTwo algorithmTwo = new AnomalyDetectorAlgorithmTwo();

        List<TemperatureReading> readings = new ArrayList<>();
        for (double temperature : new double[]{19.1, 19.2, 19.5, 19.7, 19.3, 25.1, 18.2, 19.1, 19.2, 25.37}) {
            readings.add(new TemperatureReading(temperature, "roomId", "thermometerId", Instant.now()));
        }

        Optional<Anomaly> anomaly = algorithmTwo.apply(readings);
        assertFalse(anomaly.isPresent(), "Anomaly should not be detected");
    }

    @Test
    public void testAnomalyDetected() {
        AnomalyDetectorAlgorithmTwo algorithmTwo = new AnomalyDetectorAlgorithmTwo();

        List<TemperatureReading> readings = new ArrayList<>();
        for (double temperature : new double[]{19.1, 19.2, 19.5, 19.7, 19.3, 25.1, 18.2, 19.1, 19.2, 30.4}) {
            readings.add(new TemperatureReading(temperature, "roomId", "thermometerId", Instant.now()));
        }

        Optional<Anomaly> anomaly = algorithmTwo.apply(readings);
        assertTrue(anomaly.isPresent(), "Anomaly should be detected");
    }

}
