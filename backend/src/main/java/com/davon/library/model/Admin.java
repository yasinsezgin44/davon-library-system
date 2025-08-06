package com.davon.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "admins")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "admin_level")
    private Integer adminLevel;

    @Column(length = 100)
    private String department;

    @Column(columnDefinition = "TEXT")
    private String permissions;

    @Column(name = "last_activity")
    private LocalDate lastActivity;
}
