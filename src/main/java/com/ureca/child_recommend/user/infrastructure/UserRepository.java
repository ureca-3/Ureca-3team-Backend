package com.ureca.child_recommend.user.infrastructure;


import com.ureca.child_recommend.user.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users,Long> {
    Optional<Users> findByOauthInfoOid(String oid);

    Optional<Users> findById(Long userId);
}
