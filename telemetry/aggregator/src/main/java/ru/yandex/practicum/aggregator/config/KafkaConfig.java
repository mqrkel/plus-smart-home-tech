package ru.yandex.practicum.aggregator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfig {
    private String bootstrapServer;
    private ConsumerConfig consumer;
    private ProducerConfig producer;

    @Getter
    @Setter
    public static class ConsumerConfig {
        private String clientId;
        private String groupId;
        private String topic;
        private Integer maxPollIntervalMs;
        private Boolean enableAutoCommit;
        public Long pollTimeoutMs;
    }

    @Getter
    @Setter
    public static class ProducerConfig {
        private String topic;
    }
}