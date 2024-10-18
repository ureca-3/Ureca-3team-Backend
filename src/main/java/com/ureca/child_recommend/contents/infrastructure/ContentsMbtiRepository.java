package com.ureca.child_recommend.contents.infrastructure;

import com.ureca.child_recommend.contents.domain.ContentsMbtiScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentsMbtiRepository extends JpaRepository<ContentsMbtiScore, Long> {

}