package ua.edu.viti.military.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Military Transport API - [Варіант B]")
                        .version("1.0.0")
                        .description(
                                "REST API для системи управління військовим транспортом.\n\n" +
                                        "**Функціональність:**\n" +
                                        "- 🚛 Облік транспортних засобів (реєстрація, списання)\n" +
                                        "- 🪪 Управління водіями та їх правами\n" +
                                        "- 🛠 Контроль технічного обслуговування (ТО)\n" +
                                        "- 🔍 Пошук техніки за пробігом, статусом та категорією\n\n" +
                                        "**Безпека:** Використовується JWT Token. Натисніть кнопку 'Authorize' і введіть токен."
                        )
                        .contact(new Contact()
                                .name("Курсант Чернікова")
                                .email("student@viti.edu.ua")
                        )
                        .license(new License()
                                .name("VITI Educational License")
                        )
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")
                ))
                // === НАЛАШТУВАННЯ JWT (Кнопка "Authorize") ===
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createSecurityScheme()));
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name("Bearer Authentication")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }
}