package ua.edu.viti.military.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        // === 1. ВИПРАВЛЕННЯ СЕРІАЛІЗАЦІЇ (ЛОГІКА З RedisConfig.java) ===
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // РЕЄСТРУЄМО ПІДТРИМКУ JAVA TIME

        // Серіалізатор, який підтримує LocalDateTime
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);
        // =============================================================

        // 2. Базова конфігурація (за замовчуванням 1 година)
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()
                        )
                )
                // ВИКОРИСТОВУЄМО НАШ ВИПРАВЛЕНИЙ СЕРІАЛІЗАТОР
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                jsonSerializer
                        )
                );

        // 3. Специфічні налаштування для різних кешів
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put("vehicleCategories", defaultConfig.entryTtl(Duration.ofHours(24)));
        cacheConfigurations.put("vehicles", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("drivers", defaultConfig.entryTtl(Duration.ofHours(6)));

        // 4. Створення менеджера кешу
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}