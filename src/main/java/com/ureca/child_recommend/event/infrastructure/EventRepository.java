package com.ureca.child_recommend.event.infrastructure;

import com.ureca.child_recommend.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event , Long> {
    Optional<Event> findEventByDate(LocalDate date);
}
