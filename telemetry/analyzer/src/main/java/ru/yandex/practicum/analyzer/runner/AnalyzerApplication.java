package ru.yandex.practicum.analyzer.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "ru.yandex.practicum.analyzer")
@EnableJpaRepositories(basePackages = "ru.yandex.practicum.analyzer.domain.repository")
@EntityScan(basePackages = "ru.yandex.practicum.analyzer.domain.model")

public class AnalyzerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalyzerApplication.class, args);
    }
}
