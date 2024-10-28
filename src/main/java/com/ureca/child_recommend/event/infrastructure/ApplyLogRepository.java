package com.ureca.child_recommend.event.infrastructure;

import com.ureca.child_recommend.event.domain.ApplyLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplyLogRepository extends JpaRepository<ApplyLog, Long> {
}
