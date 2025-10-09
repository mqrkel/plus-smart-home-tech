package ru.yandex.practicum.analyzer.infrastructure.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.domain.service.AnalyzerService;
import ru.yandex.practicum.analyzer.config.KafkaConfig;
import ru.yandex.practicum.analyzer.infrastructure.consumer.SnapshotConsumer;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SnapshotListenerImpl implements SnapshotListener {

    private final KafkaConfig.ConsumerConfig consumerConfig;
    private final SnapshotConsumer snapshotConsumer;
    private final AnalyzerService service;

    private volatile boolean running = false;

    @Autowired
    public SnapshotListenerImpl(KafkaConfig kafkaConfig,
                                SnapshotConsumer snapshotConsumer,
                                AnalyzerService service) {
        this.consumerConfig = kafkaConfig.getSnapshotsConsumer();
        this.snapshotConsumer = snapshotConsumer;
        this.service = service;
    }

    @Override
    public void run() {
        running = true;
        log.debug("SnapshotListenerImpl.run");

        snapshotConsumer.subscribe(List.of(consumerConfig.getTopic()));
        long pollTimeoutMs = consumerConfig.pollTimeoutMs;
        try {
            while (running) {
                ConsumerRecords<String, SensorsSnapshotAvro> records =
                        snapshotConsumer.poll(Duration.ofMillis(pollTimeoutMs));

                if (records.isEmpty())
                    continue;

                Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
                int processed = 0;

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    try {
                        log.debug("Received snapshot record {}", record);

                        service.handle(record.value());
                        processed++;

                        updateOffsets(record, offsets);
                        if (processed % 10 == 0)
                            commitOffsets(offsets, processed);
                    } catch (Exception e) {
                        log.error("Failed to process record: topic={}, partition={}, offset={}",
                                record.topic(), record.partition(), record.offset(), e);
                    }
                }

                commitOffsets(offsets, processed);
            }
        } catch (WakeupException e) {
            log.info("Snapshot consumer wakeup received - graceful shutdown");
        } catch (Exception e) {
            log.error("Unexpected error in consumer loop", e);
        } finally {
            log.info("Close Snapshot consumer");
            snapshotConsumer.close();
        }
    }


    private static void updateOffsets(ConsumerRecord<String, SensorsSnapshotAvro> record,
                                      Map<TopicPartition, OffsetAndMetadata> offsets) {
        offsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset(), "")
        );
    }

    private void commitOffsets(Map<TopicPartition, OffsetAndMetadata> offsets, int processedCount) {
        if (offsets.isEmpty())
            return;

        snapshotConsumer.commitAsync(offsets, (map, exception) -> {
            if (exception != null) {
                log.error("Failed to commit offsets for {} records", processedCount, exception);
            }
        });
        offsets.clear();
    }

    public void stop() {
        running = false;
        snapshotConsumer.wakeup();
    }
}
