package com.ureca.child_recommend.child.infrastructure;

import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildRepository extends JpaRepository<Child,Long> {
}
