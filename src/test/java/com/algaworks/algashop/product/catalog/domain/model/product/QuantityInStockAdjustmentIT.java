package com.algaworks.algashop.product.catalog.domain.model.product;

import com.algaworks.algashop.product.catalog.TestcontainerMongoDBConfig;
import com.algaworks.algashop.product.catalog.domain.model.category.ProductRepository;
import com.algaworks.algashop.product.catalog.infrastructure.persistence.MongoConfig;
import com.algaworks.algashop.product.catalog.infrastructure.persistence.dataload.DataLoadProperties;
import com.algaworks.algashop.product.catalog.infrastructure.persistence.dataload.DataLoader;
import com.algaworks.algashop.product.catalog.infrastructure.persistence.product.QuantityInStockAdjustmentMongoDBImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataMongoTest
@Import({
        MongoConfig.class,
        QuantityInStockAdjustmentMongoDBImpl.class,
        DataLoader.class,
        DataLoadProperties.class,
        TestcontainerMongoDBConfig.class
})
class QuantityInStockAdjustmentIT {

    private final static UUID existingProduct = UUID.fromString("946cea3b-d11d-4f11-b88d-3089b4e74087");

    @Autowired
    private QuantityInStockAdjustment quantityInStockAdjustment;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DataLoader dataLoader;

    @BeforeEach
    public void beforeEach() {
        dataLoader.run(new DefaultApplicationArguments());
    }

    @Test
    public void shouldIncreaseQuantity() {
        var product = productRepository.findById(existingProduct).orElseThrow();

        quantityInStockAdjustment.increase(existingProduct, 25);
        quantityInStockAdjustment.increase(existingProduct, 25);

        var productUpdated = productRepository.findById(existingProduct).orElseThrow();

        assertThat(product.getQuantityInStock()).isEqualTo(50);
        assertThat(productUpdated.getQuantityInStock()).isEqualTo(100);
    }

    @Test
    public void shouldDecreaseQuantity() {
        var product = productRepository.findById(existingProduct).orElseThrow();

        quantityInStockAdjustment.decrease(existingProduct, 25);
        quantityInStockAdjustment.decrease(existingProduct, 25);

        var productUpdated = productRepository.findById(existingProduct).orElseThrow();
        assertThat(product.getQuantityInStock()).isEqualTo(50);
        assertThat(productUpdated.getQuantityInStock()).isEqualTo(0);
    }

    @Test
    public void shouldNotDecreaseQuantity() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(()-> quantityInStockAdjustment.decrease(existingProduct, 100));
        var product = productRepository.findById(existingProduct).orElseThrow();
        assertThat(product.getQuantityInStock()).isEqualTo(50);
    }

    @Test
    public void shouldCalculateResultDecrease() {
        var product = productRepository.findById(existingProduct).orElseThrow();
        var result = quantityInStockAdjustment.decrease(product.getId(), 40);
        assertThat(result.newQuantity()).isEqualTo(10);
        assertThat(result.previousQuantity()).isEqualTo(50);
    }

    @Test
    public void shouldCalculateResultIncrease() {
        var product = productRepository.findById(existingProduct).orElseThrow();
        var result = quantityInStockAdjustment.increase(product.getId(), 40);
        assertThat(result.newQuantity()).isEqualTo(90);
        assertThat(result.previousQuantity()).isEqualTo(50);
    }


}