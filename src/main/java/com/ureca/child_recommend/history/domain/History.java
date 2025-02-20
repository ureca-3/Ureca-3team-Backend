package com.ureca.child_recommend.history.domain;

import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.child.domain.ChildMbtiScore;
import com.ureca.child_recommend.global.entity.BaseTimeEntity;
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
public class History extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @Column
    private Integer eiScore;

    @Column
    private Integer snScore;

    @Column
    private Integer tfScore;

    @Column
    private Integer jpScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;

    public static History enrollToHistory(ChildMbtiScore score) {
        return History.builder()
                .eiScore(score.getEiScore())
                .snScore(score.getSnScore())
                .tfScore(score.getTfScore())
                .jpScore(score.getJpScore())
                .child(score.getChild())
                .build();
    }
}
