package com.algaworks.algashop.product.catalog.application.category.management;

import com.algaworks.algashop.product.catalog.application.category.event.CategoryUpdatedEvent;
import com.algaworks.algashop.product.catalog.application.category.query.CategoryInput;
import com.algaworks.algashop.product.catalog.application.ApplicationMessagePublisher;
import com.algaworks.algashop.product.catalog.domain.model.category.Category;
import com.algaworks.algashop.product.catalog.domain.model.category.CategoryNotFoundException;
import com.algaworks.algashop.product.catalog.domain.model.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryManagementApplicationService {

    private final ApplicationMessagePublisher applicationMessagePublisher;
    private final CategoryRepository categoryRepository;

    @CacheEvict(value = "algashop:categories-filter:v1", key = "'default'")
    public UUID create(CategoryInput input) {
        var category = Category.of(input.getName(), input.getEnabled());
        categoryRepository.save(category);
        return category.getId();
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "algashop:categories-filter:v1", key = "'default'"),
                    @CacheEvict(value = "algashop:categories:v1", key = "#categoryId")
            }
    )
    public void update(UUID categoryId, CategoryInput input) {
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> CategoryNotFoundException.byID(categoryId));
        category.setName(input.getName());
        category.setEnabled(input.getEnabled());
        categoryRepository.save(category);

        applicationMessagePublisher.send( CategoryUpdatedEvent.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .enabled(category.getEnabled())
                .build());
    }

    public void disable(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> CategoryNotFoundException.byID(categoryId));
        category.setEnabled(false);
        categoryRepository.save(category);

        applicationMessagePublisher.send( CategoryUpdatedEvent.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .enabled(category.getEnabled())
                .build());
    }

}
