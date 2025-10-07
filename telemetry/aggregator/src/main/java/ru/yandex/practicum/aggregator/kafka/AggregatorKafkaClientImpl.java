package ru.yandex.practicum.aggregator.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.config.KafkaConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.serializer.GeneralAvroSerializer;
import ru.yandex.practicum.deserializer.SensorEventAvroDeserializer;

import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorKafkaClientImpl implements AggregatorKafkaClient {

    private Producer<String, SpecificRecordBase> producer;
    private Consumer<String, SensorEventAvro> consumer;

    private final KafkaConfig kafkaConfig;

    @Override
    public Producer<String, SpecificRecordBase> getProducer() {
        if (producer == null) {
            initProducer();
        }
        return producer;
    }

    @Override
    public Consumer<String, SensorEventAvro> getConsumer() {
        if (consumer == null) {
            initConsumer();
        }
        return consumer;
    }

    @Override
    public void stop() {
        if (consumer != null) {
            consumer.wakeup();
        }

        if (producer != null) {
            producer.flush();
            producer.close();
        }
    }

    private void initProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServer());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);
        producer = new KafkaProducer<>(config);
    }

    private void initConsumer() {
        KafkaConfig.ConsumerConfig properties = kafkaConfig.getConsumer();

        Properties config = new Properties();
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, properties.getClientId());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getGroupId());
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServer());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventAvroDeserializer.class.getCanonicalName());
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, properties.getMaxPollIntervalMs());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, properties.getEnableAutoCommit());
        consumer = new KafkaConsumer<>(config);
    }
}