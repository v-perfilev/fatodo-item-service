package contracts.permissioncontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'get allowed to admin item ids by ids'
    description 'should return status 200 and list of UUIDs'
    request {
        method GET()
        url($(
                consumer(regex("/api/permission/item/\\S{4,5}/ids\\?ids=.*")),
                producer("/api/permission/item/ADMIN/ids?ids=8a51fdaa-189c-4959-9016-ae79adfe0320")
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
                "8a51fdaa-189c-4959-9016-ae79adfe0320"
        ])
    }
}
