package ru.yandex.practicum.aggregator;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@ConfigurationProperties
public class AggregatorApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(AggregatorApplication.class, args);
        AggregationStarter aggregationStarter = configurableApplicationContext.getBean(AggregationStarter.class);
        aggregationStarter.start();
    }
}
