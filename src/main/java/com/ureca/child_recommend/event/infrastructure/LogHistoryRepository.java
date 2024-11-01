package com.ureca.child_recommend.event.infrastructure;

import com.ureca.child_recommend.event.domain.Enum.ApplyLogStatus;
import com.ureca.child_recommend.event.domain.Event;
import com.ureca.child_recommend.event.domain.LogHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LogHistoryRepository extends JpaRepository<LogHistory, Long> {

    List<LogHistory> findAllByLog(LocalDateTime log);

    List<LogHistory> findAllByEventIn(List<Event> events);
}
