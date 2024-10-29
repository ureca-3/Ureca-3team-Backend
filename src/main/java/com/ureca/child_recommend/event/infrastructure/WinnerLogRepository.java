package com.ureca.child_recommend.event.infrastructure;

import com.ureca.child_recommend.event.domain.WinnerLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WinnerLogRepository extends JpaRepository<WinnerLog, Long> {
}
