package com.ureca.child_recommend.contents.domain;

import com.ureca.child_recommend.contents.domain.Enum.ContentsStatus;
import com.ureca.child_recommend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Contents extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contents_id")
    private Long id;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String author;

    @Column
    private String publisher;

    @Column
    private LocalDate publicationYear;

    @Column
    private String contentsMbtiResult;

    @Enumerated(EnumType.STRING)
    private ContentsStatus status;

    @OneToOne
    @JoinColumn(name = "contentsMbti_id")
    private ContentsMbtiScore contentsMbti;

//    @OneToMany(mappedBy = "contents",cascade = CascadeType.ALL)
//    List<FeedBack> feedBackList = new ArrayList<>();

}
