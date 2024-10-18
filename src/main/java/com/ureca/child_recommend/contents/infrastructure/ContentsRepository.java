package com.ureca.child_recommend.contents.infrastructure;

import com.ureca.child_recommend.contents.domain.Contents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentsRepository extends JpaRepository<Contents, Long> {

    Contents findByTitleAndAuthor(String title, String author);
}
