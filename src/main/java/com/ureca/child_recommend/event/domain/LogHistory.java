package com.ureca.child_recommend.event.domain;

import com.ureca.child_recommend.event.domain.Enum.LogHistoryStatus;
import com.ureca.child_recommend.global.entity.BaseTimeEntity;
import com.ureca.child_recommend.user.domain.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class LogHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @Column
    private String name;

    @Column
    private String phone;

    @Column
    private LocalDateTime log;

    @Column
    private LogHistoryStatus status; // ACTIVE , NONACTIVE


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    public void updateStatus(LogHistoryStatus newStatus) {
        this.status = newStatus;
    }


}
