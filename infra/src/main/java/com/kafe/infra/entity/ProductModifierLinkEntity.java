package com.kafe.infra.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="product_modifier_link")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@IdClass(ProductModifierLinkEntity.Pk.class)
public class ProductModifierLinkEntity {

  @Id @Column(name="product_id") Long productId;
  @Id @Column(name="group_id")   Long groupId;

  @Data @NoArgsConstructor @AllArgsConstructor
  public static class Pk implements java.io.Serializable {
    private Long productId; private Long groupId;
  }
}
