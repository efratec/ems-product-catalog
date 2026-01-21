package contracts.category

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method PUT()
        urlPath("/api/v1/categories/7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b8e")
        headers {
            contentType('application/json')
        }
        body([
                name   : "Desktops",
                enabled: false
        ])
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