package com.algaworks.algashop.product.catalog.infrastructure.persistence.product;

import com.algaworks.algashop.product.catalog.domain.model.product.Product;
import com.algaworks.algashop.product.catalog.domain.model.product.ProductNotFoundException;
import com.algaworks.algashop.product.catalog.domain.model.product.QuantityInStockAdjustment;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class QuantityInStockAdjustmentMongoDBImpl implements QuantityInStockAdjustment {

    public static final String QUANTITY_IN_STOCK = "quantityInStock";
    public static final String ID = "id";
    public static final String VERSION = "version";
    public static final String UPDATED_AT = "updatedAt";
    public static final String FAILED_TO_UPDATE_STOCK_OF_PRODUCT_S = "Failed to update stock of product %s";

    private final MongoOperations mongoOperations;

    @Override
    public Result increase(UUID productId, int quantity) {
        var query = Query.query(Criteria.where(ID).is(productId));
        return changeStockQuantity(productId, quantity, query);
    }

    @Override
    public Result decrease(UUID productId, int quantity) {
        var query = Query.query(Criteria.where(ID).is(productId)
                .and(QUANTITY_IN_STOCK).gte(quantity));
        return changeStockQuantity(productId, quantity * -1, query);
    }

    private Result changeStockQuantity(UUID productId, int quantity, Query queryForUpdate) {

        var findProductQuantity = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(ID).is(productId)),
                Aggregation.project(QUANTITY_IN_STOCK)
        );

        var productBeforeUpdate = mongoOperations.aggregate(findProductQuantity, Product.class,
                Document.class).getUniqueMappedResult();

        if (productBeforeUpdate == null) {
            throw new ProductNotFoundException(productId);
        }

        Integer previousQuantity = productBeforeUpdate.getInteger(QUANTITY_IN_STOCK);

        var update = new Update()
                .inc(QUANTITY_IN_STOCK, quantity)
                .inc(VERSION, 1)
                .set(UPDATED_AT, OffsetDateTime.now());

        var productUpdated = mongoOperations.findAndModify(queryForUpdate, update,
                new FindAndModifyOptions().returnNew(true), Product.class);

        if (productUpdated == null) {
            throw new StockUpdateFailed(String.format(FAILED_TO_UPDATE_STOCK_OF_PRODUCT_S, productId));
        }

        return new Result(productId, previousQuantity, productUpdated.getQuantityInStock());
    }

}
