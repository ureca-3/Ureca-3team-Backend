package com.ureca.child_recommend.child.domain;

import com.ureca.child_recommend.child.domain.Enum.ChildMbtiStatus;
import com.ureca.child_recommend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.mapping.ToOne;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ChildMbti extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "childMbti_id")
    private Long id;

    @Column
    private String mbtiResult;

    @Enumerated(EnumType.STRING)
    private ChildMbtiStatus status;

    @ManyToOne
    @JoinColumn(name = "child_id")
    private Child child;

    public void updateStatus(ChildMbtiStatus childMbtiStatus) {
        this.status = childMbtiStatus;
    }
}
