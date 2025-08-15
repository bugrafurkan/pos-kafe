package com.kafe.infra.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="product_modifier_group")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ModifierGroupEntity {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
  @Column(nullable=false, length=120) String name;
  @Column(nullable=false) boolean required = false;
  @Column(name="max_select", nullable=false) int maxSelect = 1;
}
