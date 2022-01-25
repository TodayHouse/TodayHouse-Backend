package com.todayhouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TodayhouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodayhouseApplication.class, args);
    }

}
