package contracts.product

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method PUT()
        headers {
            accept "application/json"
            contentType 'application/json'
        }
        urlPath("/api/v1/products/c7e42a19-8b54-4c92-9d2a-1f8ef83a37e6") {
            body([
                    name        : value(
                            test("Notebook X11"),
                            stub(nonBlank())
                    ),
                    brand       : value(
                            test("Deep Diver"),
                            stub(nonBlank())
                    ),
                    regularPrice: value(
                            test(1500.00),
                            stub(number())
                    ),
                    salePrice   : value(
                            test(1000.00),
                            stub(number())
                    ),
                    enabled     : value(
                            test(false),
                            stub(anyBoolean())
                    ),
                    categoryId  : value(
                            test("f5ab7a1e-37da-41e1-892b-a1d38275c2f2"),
                            stub(anyUuid())
                    ),
                    description : value(
                            test("A Gamer Notebook"),
                            stub(optional(nonBlank()))
                    )
            ])
        }
    }
    response {
        status 404
        body([
                instance: fromRequest().path(),
                type    : "/errors/not-found",
                title   : "Not found"
        ])
    }

}