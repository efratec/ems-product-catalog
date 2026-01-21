package contracts.product

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    request {
        method DELETE()
        headers {
            accept 'application/json'
        }
        url("/api/v1/products/7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b2e")
    }

    response {
        status 404
        body([
                instance: fromRequest().path(),
                type: "/errors/not-found",
                title: "Not found"
        ])
    }
}