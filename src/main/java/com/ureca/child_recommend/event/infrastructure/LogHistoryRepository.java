package com.ureca.child_recommend.event.infrastructure;

import com.ureca.child_recommend.event.domain.LogHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogHistoryRepository extends JpaRepository<LogHistory, Long> {
}
