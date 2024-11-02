package com.ureca.child_recommend.event.infrastructure;

import com.ureca.child_recommend.event.domain.ApplyLog;
import com.ureca.child_recommend.event.domain.Enum.ApplyLogStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplyLogRepository extends JpaRepository<ApplyLog, Long> {
        List<ApplyLog> findAllByStatus(ApplyLogStatus applyLogStatus);
        List<ApplyLog> findAllByOrderByLogAsc();
}
