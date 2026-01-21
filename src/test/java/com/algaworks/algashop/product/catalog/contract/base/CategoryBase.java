package com.algaworks.algashop.product.catalog.contract.base;

import com.algaworks.algashop.product.catalog.application.PageModel;
import com.algaworks.algashop.product.catalog.application.ResourceNotFoundException;
import com.algaworks.algashop.product.catalog.application.category.CategoryOutputTestFixture;
import com.algaworks.algashop.product.catalog.application.category.management.CategoryManagementApplicationService;
import com.algaworks.algashop.product.catalog.application.category.query.CategoryDetailOutput;
import com.algaworks.algashop.product.catalog.application.category.query.CategoryInput;
import com.algaworks.algashop.product.catalog.application.category.query.CategoryQueryService;
import com.algaworks.algashop.product.catalog.presentation.CategoryController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = CategoryController.class)
public class CategoryBase {

    private static final UUID createdCategoryId = UUID.randomUUID();
    private static final UUID validCategoryId = UUID.fromString("7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b2e");
    private static final UUID invalidCategoryId = UUID.fromString("7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b6e");
    private static final UUID invalidUpdateCategoryId = UUID.fromString("7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b8e");
    private static final UUID invalidDeleteCategoryId = UUID.fromString("7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b9e");

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private CategoryQueryService categoryQueryService;

    @MockitoBean
    private CategoryManagementApplicationService categoryManagementService;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build());

        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

        Mockito.when(categoryQueryService.filter(Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer(answer -> {
                    Integer page = answer.getArgument(0);
                    Integer size = answer.getArgument(1);

                    return PageModel.<CategoryDetailOutput>builder()
                            .number(page)
                            .size(size)
                            .totalPages(1)
                            .totalElements(2)
                            .content(List.of(
                                    CategoryOutputTestFixture.aCategory().build(),
                                    CategoryOutputTestFixture.aDisabledCategory().build()
                            ))
                            .build();
                });


        Mockito.when(categoryQueryService.findById(validCategoryId))
                .thenReturn(CategoryOutputTestFixture.aCategory().id(validCategoryId).build());

        Mockito.when(categoryManagementService.create(Mockito.any(CategoryInput.class)))
                .thenReturn(createdCategoryId);

        Mockito.when(categoryQueryService.findById(createdCategoryId))
                .thenReturn(CategoryOutputTestFixture.aCategory().id(createdCategoryId).build());

        when(categoryQueryService.findById(invalidCategoryId))
                .thenThrow(new ResourceNotFoundException());

        Mockito.doThrow(new ResourceNotFoundException())
                .when(categoryManagementService)
                .update(any(CategoryInput.class), eq(invalidUpdateCategoryId));

        Mockito.doThrow(new ResourceNotFoundException())
                .when(categoryManagementService)
                .disable(eq(invalidDeleteCategoryId));
    }

}
