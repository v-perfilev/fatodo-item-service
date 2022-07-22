package contracts.permissioncontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'get allowed to admin item ids by ids'
    description 'should return status 200 and list of UUIDs'
    request {
        method POST()
        url($(
                consumer(regex("/api/permissions/items/.*/ids")),
                producer("/api/permissions/items/ADMIN/ids")
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
                producer(["8a51fdaa-189c-4959-9016-ae79adfe0320"])
        ))
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body([
                "8a51fdaa-189c-4959-9016-ae79adfe0320"
        ])
    }
}
