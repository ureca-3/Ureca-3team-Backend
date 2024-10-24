package com.ureca.child_recommend.contents.domain;

import com.ureca.child_recommend.contents.domain.Enum.ContentsStatus;
import com.ureca.child_recommend.contents.presentation.dto.ContentsDto;
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
    private String posterUrl;

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

    public static Contents saveContents(ContentsDto.Request request, ContentsMbtiScore mbtiScore, String mbtiResult) {
        return Contents.builder()
                .title(request.getTitle())
                .posterUrl(request.getPosterUrl())
                .description(request.getDescription())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .publicationYear(request.getPublicationYear())
                .contentsMbtiResult(mbtiResult)
                .status(ContentsStatus.ACTIVE)
                .contentsMbti(mbtiScore)
                .build();
    }

    public void  updateContents(ContentsDto.Request request) {
        if (request.getTitle() != null) {
            this.title = request.getTitle();
        }
        if (request.getPosterUrl() != null) {
            this.posterUrl = request.getPosterUrl();
        }
        if (request.getDescription() != null) {
            this.description = request.getDescription();
        }
        if (request.getAuthor() != null) {
            this.author = request.getAuthor();
        }
        if (request.getPublisher() != null) {
            this.publisher = request.getPublisher();
        }
        if (request.getPublicationYear() != null) {
            this.publicationYear = request.getPublicationYear();
        }
        if (request.getStatus() != null) {
            this.status = request.getStatus();
        }
    }

    public void updateStatus(ContentsStatus status) {
        this.status = status;
    }
}
