package ua.edu.viti.military;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing; // <--- Імпорт
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing // <--- ДОДАЙ ЦЕЙ РЯДОК! Без нього дати будуть null.
@EnableAsync
public class MilitaryApplication {

    public static void main(String[] args) {
        SpringApplication.run(MilitaryApplication.class, args);
    }

}