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
import ru.yandex.practicum.analyzer.infrastructure.consumer.HubEventConsumer;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class HubEventListenerImpl implements HubEventListener {

    private final KafkaConfig.ConsumerConfig consumerConfig;
    private final HubEventConsumer hubEventConsumer;
    private final AnalyzerService service;

    private volatile boolean running = false;

    @Autowired
    public HubEventListenerImpl(KafkaConfig kafkaConfig,
                                HubEventConsumer hubEventConsumer,
                                AnalyzerService service) {
        this.consumerConfig = kafkaConfig.getHubEventsConsumer();
        this.hubEventConsumer = hubEventConsumer;
        this.service = service;
    }

    @Override
    public void run() {
        log.debug("HubEventListenerImpl.run");
        running = true;

        try {
            hubEventConsumer.subscribe(List.of(consumerConfig.getTopic()));
            long pollTimeoutMs = consumerConfig.pollTimeoutMs;
            while (running) {

                ConsumerRecords<String, HubEventAvro> records =
                        hubEventConsumer.poll(Duration.ofMillis(pollTimeoutMs));
                if (!records.isEmpty()) {
                    Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
                    int processed = 0;

                    for (ConsumerRecord<String, HubEventAvro> record : records) {
                        try {
                            log.debug("Received hubEvent record {}", record);

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
            }
        } catch (WakeupException e) {
            log.info("Hub events consumer wakeup received - graceful shutdown");
        } catch (Exception e) {
            log.error("Unexpected error in Hub events consumer loop", e);
        } finally {
            log.info("Close Hub events consumer");
            hubEventConsumer.close();
        }
    }

    private static void updateOffsets(ConsumerRecord<String, HubEventAvro> record,
                                      Map<TopicPartition, OffsetAndMetadata> offsets) {
        offsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset(), "")
        );
    }

    private void commitOffsets(Map<TopicPartition, OffsetAndMetadata> offsets, int processedCount) {
        if (offsets.isEmpty())
            return;

        hubEventConsumer.commitAsync(offsets, (map, exception) -> {
            if (exception != null) {
                log.error("Failed to commit offsets for {} records", processedCount, exception);
            }
        });
        offsets.clear();
    }

    public void stop() {
        running = false;
        hubEventConsumer.wakeup();
    }
}
