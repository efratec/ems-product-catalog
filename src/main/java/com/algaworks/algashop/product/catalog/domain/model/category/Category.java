package com.algaworks.algashop.product.catalog.domain.model.category;

import com.algaworks.algashop.product.catalog.domain.model.GeneratorId;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Document(collection = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    private String name;

    private Boolean enabled;

    @Version
    private Long version;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;

    @CreatedBy
    private UUID createdByUserId;

    @LastModifiedBy
    private UUID lastModifiedByUserId;

    private Category(String name, Boolean enabled) {
        this.id = GeneratorId.generateTimeBasedUUID();
        this.setName(name);
        this.setEnabled(enabled);
    }

    public static Category of(String name, Boolean enabled) {
        return new Category(name, enabled);
    }

    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException();
        }
        this.name = name;
    }

    public void setEnabled(Boolean enabled) {
        Objects.requireNonNull(enabled);
        this.enabled = enabled;
    }

}
