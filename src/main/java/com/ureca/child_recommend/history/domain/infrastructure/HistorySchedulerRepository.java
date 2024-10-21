package com.ureca.child_recommend.history.domain.infrastructure;

import com.ureca.child_recommend.history.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorySchedulerRepository extends JpaRepository<History, Long> {
    List<History> findByChildId(Long id);
}
