package com.kafe.infra;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.kafe")
@EnableJpaRepositories(basePackages = "com.kafe.infra.repo")
@EntityScan(basePackages = "com.kafe.infra.entity")
public class TestConfig {
}
