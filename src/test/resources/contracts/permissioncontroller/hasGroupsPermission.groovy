package contracts.permissioncontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'can admin groups by ids'
    description 'should return status 200 and boolean'
    request {
        method POST()
        url($(
                consumer(regex("/api/permissions/groups/[\\S]{4,5}")),
                producer("/api/permissions/groups/ADMIN")
        ))
        headers {
            contentType applicationJson()
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ZjlhN2NhZS03M2M4LTRhZDYtYjEzNS01YmQxMDliNTFkMmUiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.Go0MIqfjREMHOLeqoX2Ej3DbeSG7ZxlL4UAvcxqNeO-RgrKUCrgEu77Ty1vgR_upxVGDAWZS-JfuSYPHSRtv-w")
            )
        }
        body($(
                consumer(regex(".+")),
                producer(["12886ad8-f1a2-487c-a5f1-ff71d63a3b52"])
        ))
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body("true")
    }
}
