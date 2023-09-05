package io.kontakt.apps.anomaly.detector;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.shaded.org.awaitility.Durations;
import org.testcontainers.shaded.org.awaitility.core.ConditionTimeoutException;

import java.io.Closeable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestKafkaConsumer<V> implements Closeable {
    Logger logger = LoggerFactory.getLogger(TestKafkaConsumer.class);
    protected Consumer<String, V> consumer;

    public TestKafkaConsumer(String bootstrapServers, String outputTopicName, Class<V> valueClass) {
        Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps(bootstrapServers, "testGroup", "true");
        JsonDeserializer<V> deserializer = new JsonDeserializer<>(valueClass);
        deserializer.addTrustedPackages("*");
        this.consumer = new DefaultKafkaConsumerFactory<>(consumerProperties, new StringDeserializer(), deserializer).createConsumer();
        this.consumer.subscribe(List.of(outputTopicName));
    }

    public List<V> drain(int expectedRecordCount, Duration waitAtMost) {
        return drainRecords(consumerRecords -> consumerRecords.size() == expectedRecordCount, waitAtMost)
                .stream().map(ConsumerRecord::value).collect(Collectors.toList());
    }

    public List<V> drain(Predicate<List<ConsumerRecord<String, V>>> predicate, Duration waitAtMost) {
        return drainRecords(predicate, waitAtMost).stream().map(ConsumerRecord::value).collect(Collectors.toList());
    }

    public void assertNoMoreRecords() {
        assertNoMoreRecords(Duration.ofSeconds(1L));
    }

    public void assertNoMoreRecords(Duration waitAtMost) {
        assertTrue(consumer.poll(waitAtMost).isEmpty());
    }

    public void assertNoRecords(Duration waitAtMost) {
        List<ConsumerRecord<String, V>> receivedRecords = new ArrayList<>();
        try {
            Awaitility.await()
                .atMost(waitAtMost)
                .pollInterval(Durations.FIVE_HUNDRED_MILLISECONDS)
                .until(() -> {
                    ConsumerRecords<String, V> poll = consumer.poll(Duration.ofMillis(50));
                    poll.iterator().forEachRemaining(receivedRecords::add);
                    return !receivedRecords.isEmpty();
                });
        } catch (ConditionTimeoutException e) {
            logger.info("Assertion, received records list is empty: {}", receivedRecords.isEmpty());
            assertTrue(receivedRecords.isEmpty());
        }
    }

    private List<ConsumerRecord<String, V>> drainRecords(Predicate<List<ConsumerRecord<String, V>>> predicate, Duration waitAtMost) {
        List<ConsumerRecord<String, V>> allRecords = new ArrayList<>();
        Awaitility.await()
                .atMost(waitAtMost)
                .pollInterval(Durations.ONE_HUNDRED_MILLISECONDS)
                .until(() -> {
                    ConsumerRecords<String, V> poll = consumer.poll(Duration.ofMillis(50));
                    poll.iterator().forEachRemaining(allRecords::add);
                    return predicate.test(allRecords);
                });
        return allRecords;
    }

    @Override
    public void close() {
        this.consumer.close();
    }
}
