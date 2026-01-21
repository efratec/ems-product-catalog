package com.algaworks.algashop.product.catalog.application.category.management;

import com.algaworks.algashop.product.catalog.application.category.query.CategoryInput;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CategoryManagementApplicationService {

    public UUID create(CategoryInput input) {
        return null;
    }

    public void update(CategoryInput input, UUID categoryId) {

    }

    public void disable(UUID categoryId) {

    }

}
