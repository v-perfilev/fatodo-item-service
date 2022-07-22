package contracts.checkcontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'get type and parent'
    description 'should return status 200 and TypeAndParentDTO'
    request {
        method GET()
        url($(
                consumer(regex("/api/check/type-and-parent/" + uuid().toString())),
                producer("/api/check/type-and-parent/8a51fdaa-189c-4959-9016-ae79adfe0320")
        ))
        headers {
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ZjlhN2NhZS03M2M4LTRhZDYtYjEzNS01YmQxMDliNTFkMmUiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.Go0MIqfjREMHOLeqoX2Ej3DbeSG7ZxlL4UAvcxqNeO-RgrKUCrgEu77Ty1vgR_upxVGDAWZS-JfuSYPHSRtv-w")
            )
        }
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body([
                "type"    : "ITEM",
                "parentId": "12886ad8-f1a2-487c-a5f1-ff71d63a3b52"
        ])
    }
}
