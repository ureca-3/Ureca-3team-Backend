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

    @Column
    private String description;

    @Column
    private String author;

    @Column
    private String publisher;

    @Column
    private LocalDate publicationYear;

    @Column
    private String contentsMBTIResult;

    @Enumerated(EnumType.STRING)
    private ContentsStatus status;

    @OneToOne
    @JoinColumn(name = "contentsMBTI_id")
    private ContentsMbtiScore contentsMBTI;

//    @OneToMany(mappedBy = "contents",cascade = CascadeType.ALL)
//    List<FeedBack> feedBackList = new ArrayList<>();

}
