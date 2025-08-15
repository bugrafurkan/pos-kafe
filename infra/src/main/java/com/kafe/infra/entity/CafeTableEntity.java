package com.kafe.infra.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cafe_table")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CafeTableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=32)
    private String code;

    @Column(length=120)
    private String name;

    private Integer capacity;

    // AVAILABLE, OCCUPIED, CLOSED
    @Column(nullable=false, length=24)
    private String status;
}
