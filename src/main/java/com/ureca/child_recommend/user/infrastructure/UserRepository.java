package com.ureca.child_recommend.user.infrastructure;


import com.ureca.child_recommend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByOauthInfoOid(String oid);
}
