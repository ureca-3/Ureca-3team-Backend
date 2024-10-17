package com.ureca.child_recommend.child.domain;


import com.ureca.child_recommend.child.domain.Enum.ChildMbtiScoreStatus;
import com.ureca.child_recommend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChildMbtiScore extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "childMbtiScore_id")
    private Long id;

    @Column
    private Integer eiScore;

    @Column
    private Integer snScore;

    @Column
    private Integer tfScore;

    @Column
    private Integer jpScore;

    @Column
    private LocalDate assessmentDate;

    @Enumerated(EnumType.STRING)
    private ChildMbtiScoreStatus status;

    @ManyToOne
    @JoinColumn(name = "child_id")
    private Child child;

    public void updateStatus(ChildMbtiScoreStatus childMbtiScoreStatus) {
        this.status = childMbtiScoreStatus;
    }
}
