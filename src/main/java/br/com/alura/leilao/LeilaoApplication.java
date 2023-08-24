package br.com.alura.leilao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SpringBootApplication
public class LeilaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeilaoApplication.class, args);
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
