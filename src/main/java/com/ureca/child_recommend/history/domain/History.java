package com.ureca.child_recommend.history.domain;

import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.global.entity.BaseTimeEntity;
import com.ureca.child_recommend.user.domain.User;
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

    @ManyToOne
    @JoinColumn(name = "child_id")
    private Child child;
}
