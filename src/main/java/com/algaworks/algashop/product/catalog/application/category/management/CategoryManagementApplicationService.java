package com.algaworks.algashop.product.catalog.application.category.management;

import com.algaworks.algashop.product.catalog.application.ResourceNotFoundException;
import com.algaworks.algashop.product.catalog.application.category.query.CategoryInput;
import com.algaworks.algashop.product.catalog.domain.model.category.Category;
import com.algaworks.algashop.product.catalog.domain.model.category.CategoryNotFoundException;
import com.algaworks.algashop.product.catalog.domain.model.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryManagementApplicationService {

    private final CategoryRepository categoryRepository;

    public UUID create(CategoryInput input) {
        var category = Category.of(input.getName(), input.getEnabled());
        categoryRepository.save(category);
        return category.getId();
    }

    public void update(UUID categoryId, CategoryInput input) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        category.setName(input.getName());
        category.setEnabled(input.getEnabled());
        categoryRepository.save(category);
    }

    public void disable(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        category.setEnabled(false);
        categoryRepository.save(category);
    }

}
