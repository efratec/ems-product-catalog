package com.algaworks.algashop.product.catalog.domain.model.category;

import com.algaworks.algashop.product.catalog.domain.model.DomainEntityNotFoundException;

import java.util.UUID;

public class CategoryNotFoundException extends DomainEntityNotFoundException {

    private CategoryNotFoundException(UUID categoryId) {
        super(String.format("Category with id %s was not found", categoryId));
    }

    public static CategoryNotFoundException byID(UUID categoryId) {
        return new CategoryNotFoundException(categoryId);
    }

}
