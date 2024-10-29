package com.ureca.child_recommend.contents.infrastructure;

import com.ureca.child_recommend.contents.domain.Contents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContentsRepository extends JpaRepository<Contents, Long> {

    Optional<Contents> findByTitleAndAuthor(String title, String author);

    List<Contents> findByTitleContainingOrAuthorContaining(String title, String author);

}
