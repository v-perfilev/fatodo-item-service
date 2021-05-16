package contracts.configurationcontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'set order of group'
    description 'should return status 200'
    request {
        method POST()
        url("/api/configuration/order")
        headers {
            contentType applicationJson()
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ZjlhN2NhZS03M2M4LTRhZDYtYjEzNS01YmQxMDliNTFkMmUiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.Go0MIqfjREMHOLeqoX2Ej3DbeSG7ZxlL4UAvcxqNeO-RgrKUCrgEu77Ty1vgR_upxVGDAWZS-JfuSYPHSRtv-w")
            )
        }
        body($(
                consumer(any()),
                producer(["605db3e3-9320-4ec9-999e-85da23c31e29", "12886ad8-f1a2-487c-a5f1-ff71d63a3b52"])
        ))
    }
    response {
        status 200
    }
}
