package com.algaworks.algashop.product.catalog.infrastructure.persistence.category;

import com.algaworks.algashop.product.catalog.application.PageModel;
import com.algaworks.algashop.product.catalog.application.ResourceNotFoundException;
import com.algaworks.algashop.product.catalog.application.category.query.CategoryDetailOutput;
import com.algaworks.algashop.product.catalog.application.category.query.CategoryFilter;
import com.algaworks.algashop.product.catalog.application.category.query.CategoryQueryService;
import com.algaworks.algashop.product.catalog.application.utility.Mapper;
import com.algaworks.algashop.product.catalog.domain.model.category.Category;
import com.algaworks.algashop.product.catalog.domain.model.category.CategoryRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryQueryServiceImpl implements CategoryQueryService {

    private static final String ENABLED = "enabled";
    private final MongoOperations mongoOperations;
    private final CategoryRepository categoryRepository;
    private final Mapper mapper;

    @Override
    public CategoryDetailOutput findById(UUID categoryId) {
        var category = categoryRepository.findById(categoryId).orElseThrow(ResourceNotFoundException::new);
        return mapper.convert(category, CategoryDetailOutput.class);
    }

    @Override
    public PageModel<CategoryDetailOutput> filter(CategoryFilter filter) {
        var query = queryWith(filter);
        long totalItems = mongoOperations.count(query, Category.class);

        var pageRequest = PageRequest.of(filter.getPage(), filter.getSize(), sortWith(filter));
        var pagedQuery = query.with(pageRequest);

        List<Category> categories;
        int totalPages = 0;

        if (totalItems > 0) {
            categories = mongoOperations.find(pagedQuery, Category.class);
            totalPages = (int) Math.ceil((double) totalItems / pageRequest.getPageSize());
        } else {
            categories = new ArrayList<>();
        }

        var categoriesDetailsOutput = categories.stream()
                .map(category -> mapper.convert(category, CategoryDetailOutput.class))
                .toList();

        return PageModel.<CategoryDetailOutput>builder()
                .content(categoriesDetailsOutput)
                .number(pageRequest.getPageNumber())
                .size(pageRequest.getPageSize())
                .totalElements(totalItems)
                .totalPages(totalPages)
                .build();
    }

    private Sort sortWith(CategoryFilter filter) {
        return Sort.by(filter.getSortDirectionOrDefault(),
                filter.getSortByPropertyOrDefault().getPropertyName());
    }

    private Query queryWith(CategoryFilter filter) {
        var query = new Query();

        if (filter.getEnabled() != null) {
            query.addCriteria(Criteria.where(ENABLED).is(filter.getEnabled()));
        }

        if (StringUtils.isNotBlank(filter.getName())) {
            query.addCriteria(TextCriteria.forDefaultLanguage().matching(filter.getName()));
        }

        return query;
    }

}
