package com.ureca.child_recommend.relation.domain;

import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.global.entity.BaseTimeEntity;
import com.ureca.child_recommend.relation.domain.Enum.FeedBackType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class FeedBack extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedBack_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private FeedBackType type; // LIKE, DISLIKE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contents_id")
    private Contents contents;


    public static FeedBack createFeedBack(FeedBackType feedBackType,Child child,Contents contents){
        return new FeedBack(feedBackType,child,contents);
    }

    private FeedBack(FeedBackType feedBackType,Child child,Contents contents){
        this.type = feedBackType;
        this.child =child;
        this.contents =contents;
    }

}
