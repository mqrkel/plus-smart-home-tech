package ru.yandex.practicum.aggregator;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.config.KafkaConfig;
import ru.yandex.practicum.aggregator.kafka.AggregatorKafkaClient;
import ru.yandex.practicum.aggregator.service.SnapshotService;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AggregationStarter {

    private final AggregatorKafkaClient kafkaClient;
    private final SnapshotService service;
    private final KafkaConfig kafkaConfig;

    public AggregationStarter(AggregatorKafkaClient kafkaClient,
                              SnapshotService service,
                              KafkaConfig kafkaConfig) {
        this.kafkaClient = kafkaClient;
        this.service = service;
        this.kafkaConfig = kafkaConfig;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("JVM is shooting down. Stop consumer");
            stop();
        }));
    }

    public void stop() {
        log.info("AggregationStarter::stop");
        kafkaClient.stop();
    }

    public void start() {
        log.info("AggregationStarter::start");
        try {
            this.kafkaClient.getConsumer().subscribe(List.of(kafkaConfig.getConsumer().getTopic()));
            long pollTimeoutMs = kafkaConfig.getConsumer().pollTimeoutMs;
            while (true) {

                ConsumerRecords<String, SensorEventAvro> records =
                        kafkaClient.getConsumer().poll(Duration.ofMillis(pollTimeoutMs));
                if (!records.isEmpty()) {
                    Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
                    int processed = 0;

                    for (ConsumerRecord<String, SensorEventAvro> record : records) {
                        log.debug("Received sensor record {}", record);
                        service.handle(record);
                        processed++;

                        updateOffsets(record, offsets);
                        if (processed % 10 == 0)
                            commitOffsets(offsets, processed);
                    }
                    kafkaClient.getProducer().flush();
                    commitOffsets(offsets, processed);
                }
            }
        }
        catch (WakeupException e) {
            log.error(e.getMessage());
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        finally {
            log.info("Consumer was closed");
            kafkaClient.getProducer().flush();
            kafkaClient.getProducer().close();
            kafkaClient.getConsumer().close();
        }
    }

    private static void updateOffsets(ConsumerRecord<String, SensorEventAvro> record, Map<TopicPartition, OffsetAndMetadata> offsets) {
        offsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset(), "")
        );
    }

    private void commitOffsets(Map<TopicPartition, OffsetAndMetadata> offsets, int processedCount) {
        if (offsets.isEmpty())
            return;

        kafkaClient.getConsumer().commitAsync(offsets, (map, exception) -> {
            if (exception != null) {
                log.error("Failed to commit offsets for {} records", processedCount, exception);
            }
        });
    }
}
