package ru.yandex.practicum.analyzer.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfig {
    private ConsumerConfig hubEventsConsumer;
    private ConsumerConfig snapshotsConsumer;
    private ProducerConfig producer;

    @Getter
    @Setter
    public static class ConsumerConfig {
        private String bootstrapServer;
        private String clientId;
        private String groupId;
        private String topic;
        private Integer maxPollIntervalMs;
        private Boolean enableAutoCommit;
        public Long pollTimeoutMs;

        public Properties getProperties() {
            Properties properties = new Properties();
            properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
            properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
            properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
            return properties;
        }
    }

    @Getter
    @Setter
    public static class ProducerConfig {
        private String topic;
    }
}
