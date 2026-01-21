package contracts.category

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method DELETE()
        urlPath("/api/v1/categories/7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b2e")
    }
    response {
        status 204
    }

}
