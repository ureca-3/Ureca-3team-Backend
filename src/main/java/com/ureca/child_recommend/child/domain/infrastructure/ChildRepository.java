package com.ureca.child_recommend.child.domain.infrastructure;

import com.ureca.child_recommend.child.domain.Child;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildRepository extends JpaRepository<Child, Long> {
}
