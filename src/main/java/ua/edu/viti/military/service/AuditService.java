package ua.edu.viti.military.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuditService {

    // Цей метод виконується в ОКРЕМІЙ транзакції.
    // Навіть якщо метод, який його викликав, впаде з помилкою і зробить rollback,
    // цей запис в логах (або в базі audit_logs) ЗБЕРЕЖЕТЬСЯ.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSecurityEvent(String username, String action, boolean success) {
        // У реальному проєкті тут було б: auditLogRepository.save(new Log(...));
        // Для прикладу просто пишемо в консоль, імітуючи запис в БД
        log.info("[AUDIT TRANSACTION] User: {}, Action: {}, Success: {}", username, action, success);
    }
}