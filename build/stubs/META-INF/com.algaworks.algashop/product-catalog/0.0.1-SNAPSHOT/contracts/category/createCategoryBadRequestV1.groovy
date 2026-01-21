package contracts.category

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method POST()
        url "/api/v1/categories"
        headers {
            contentType("application/json")
        }
        body([
                name: ""
        ])
    }
    response {
        status 400
        headers {
            contentType 'application/problem+json'
        }
        body([
                instance: fromRequest().path(),
                type    : "/errors/invalid-fields",
                title   : "Invalid fields",
                "detail": "One or more fields are invalid",
                fields  : [
                        name   : anyNonBlankString(),
                        enabled: anyNonBlankString()
                ]
        ])
    }

}