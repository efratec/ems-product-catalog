package com.algaworks.algashop.product.catalog.application.category;

import com.algaworks.algashop.product.catalog.application.category.query.CategoryDetailOutput;

import java.util.UUID;

public class CategoryOutputTestFixture {

    private CategoryOutputTestFixture() {
    }

    public static CategoryDetailOutput.CategoryDetailOutputBuilder aCategory() {
        return CategoryDetailOutput.builder()
                .id(UUID.randomUUID())
                .name("Electronics")
                .enabled(true);
    }

    public static CategoryDetailOutput.CategoryDetailOutputBuilder aDisabledCategory() {
        return CategoryDetailOutput.builder()
                .id(UUID.randomUUID())
                .name("Books")
                .enabled(false);
    }

}
