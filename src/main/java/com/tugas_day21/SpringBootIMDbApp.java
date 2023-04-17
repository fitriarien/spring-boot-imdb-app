package com.tugas_day21;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.tugas_day21"})
public class SpringBootIMDbApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootIMDbApp.class, args);
    }
}
