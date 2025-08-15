package com.kafe.infra.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="cafe_table")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class TableEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) 
    Long id;

    @Column(nullable=false, unique=true, length=32)
    String code;

    @Column(length=120)
    String name;

    @Column
    Integer capacity;

    @Column(nullable=false, length=24)
    @Builder.Default
    String status = "AVAILABLE"; // AVAILABLE, OCCUPIED, CLOSED
}
