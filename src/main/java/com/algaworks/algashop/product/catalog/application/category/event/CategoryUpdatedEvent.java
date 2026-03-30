package com.algaworks.algashop.product.catalog.application.category.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class CategoryUpdatedEvent {

    private UUID categoryId;
    private String name;
    private Boolean enabled;

}
