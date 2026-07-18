package com.algaworks.algashop.product.catalog.infrastructure.persistence.category;

import com.algaworks.algashop.product.catalog.application.PageModel;
import com.algaworks.algashop.product.catalog.application.ResourceNotFoundException;
import com.algaworks.algashop.product.catalog.application.category.query.CategoryDetailOutput;
import com.algaworks.algashop.product.catalog.application.category.query.CategoryFilter;
import com.algaworks.algashop.product.catalog.application.category.query.CategoryQueryService;
import com.algaworks.algashop.product.catalog.application.utility.Mapper;
import com.algaworks.algashop.product.catalog.domain.model.category.Category;
import com.algaworks.algashop.product.catalog.domain.model.category.CategoryNotFoundException;
import com.algaworks.algashop.product.catalog.domain.model.category.CategoryRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
    @Cacheable(cacheNames = "algashop:categories:v1", key = "#categoryId")
    public CategoryDetailOutput findById(UUID categoryId) {
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> CategoryNotFoundException.byID(categoryId));
        return mapper.convert(category, CategoryDetailOutput.class);
    }

    @Override
    @Cacheable(cacheNames = "algashop:categories-filter:v1",
            key = "'default'",
            condition = "#filter.isCacheable()")
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

    @Override
    public OffsetDateTime lastModified() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group().max("updatedAt").as("lastModified")
        );
        AggregationResults<Document> result = mongoOperations.aggregate(aggregation,
                "categories", Document.class);

        Document document = result.getUniqueMappedResult();

        if (document == null) {
            return OffsetDateTime.now();
        }

        return document.getDate("lastModified").toInstant().atOffset(ZoneOffset.UTC);
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
