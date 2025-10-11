package ru.yandex.practicum.aggregator.runner;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "ru.yandex.practicum.aggregator")

public class AggregatorApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(AggregatorApplication.class, args);
        AggregationStarter aggregationStarter = configurableApplicationContext.getBean(AggregationStarter.class);
        aggregationStarter.start();
    }
}
