package com.ureca.child_recommend.history.infrastructure;

import com.ureca.child_recommend.history.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query(value = "SELECT *" +
            "FROM history h " +
            "WHERE h.child_id = :childId " +
            "AND h.create_at > :startDate " +
            "ORDER BY h.create_at " +
            "LIMIT 7", nativeQuery = true)
    List<History> findTop7ByChildIdOrderByCreateAtDesc(Long childId, LocalDate startDate);

    List<History> findAllByChildId(Long childId);
}
