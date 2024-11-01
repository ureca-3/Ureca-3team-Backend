package com.ureca.child_recommend.history.infrastructure;

import com.ureca.child_recommend.history.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {

    List<History> findTop7ByChildIdAndCreateAtGreaterThanEqualOrderByCreateAtAsc(Long childId, LocalDateTime startDateTime);

    List<History> findByChildIdAndCreateAtGreaterThanEqual(Long childId, LocalDateTime startDateTime);
}
