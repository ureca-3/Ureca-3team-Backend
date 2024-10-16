package com.ureca.child_recommend.testExample.infrastructure;

import com.ureca.child_recommend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<User,Long> {
}
