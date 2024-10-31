package com.ureca.child_recommend.contents.infrastructure;

import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.contents.domain.Enum.ContentsStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContentsRepository extends JpaRepository<Contents, Long> {

    Optional<Contents> findByTitleAndAuthor(String title, String author);

    List<Contents> findByStatusAndTitleContaining(ContentsStatus status, String title);

    List<Contents> findByTitleContainsAndStatus(String title, ContentsStatus status);

    List<Contents> findByIdIn(List<Long> contentsIdList);

    @EntityGraph(attributePaths = {"contentsMbti"})
    Optional<Contents> findWithContentsScoreById(Long contentId);

}
