package com.demo.sprintw1.repository;

import com.demo.sprintw1.audit.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

}
