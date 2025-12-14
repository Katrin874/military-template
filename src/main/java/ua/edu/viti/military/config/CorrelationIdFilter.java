package ua.edu.viti.military.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.UUID;

/**
 * Фільтр, який встановлює унікальний Correlation ID (CID) для кожного вхідного запиту
 * та розміщує його в MDC (Mapped Diagnostic Context), щоб він був доступний у всіх логах.
 */
@Component
public class CorrelationIdFilter implements Filter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // 1. Отримати ID з хедеру, якщо він був переданий (для трасування між сервісами)
            String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);

            // 2. Якщо ID немає, генеруємо новий
            if (correlationId == null || correlationId.isEmpty()) {
                correlationId = UUID.randomUUID().toString();
            }

            // 3. Встановлюємо ID в MDC. Тепер він буде доданий до всіх логів.
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);

            // 4. Додаємо ID у відповідь, щоб клієнт міг його використовувати для трасування
            httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);

            // 5. Продовжуємо обробку запиту
            chain.doFilter(request, response);

        } finally {
            // 6. ОЧИЩАЄМО MDC. Це критично, щоб уникнути витоку контексту між потоками.
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }
}