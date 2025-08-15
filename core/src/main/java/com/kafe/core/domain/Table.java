package com.kafe.core.domain;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Table {
    private Long id;
    private String name;
    private TableStatus status = TableStatus.FREE;
    private Long currentOrderId;
}
