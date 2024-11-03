package com.ureca.child_recommend.event.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ureca.child_recommend.event.domain.Enum.ApplyLogStatus;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_id")
    private Long id;

    @Column
    private String name;

    @Column
    private String phone;

    @Column
    private LocalDateTime log;

    @Enumerated(EnumType.STRING)
    private ApplyLogStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;


    public static ApplyLog create(String name, String phone,LocalDateTime now,ApplyLogStatus status,  Users user, Event event) {
        return ApplyLog.builder()
                .name(name)
                .phone(phone)
                .log(LocalDateTime.now())
                .status(status)
                .user(user)
                .event(event)
                .build();
    }

}
