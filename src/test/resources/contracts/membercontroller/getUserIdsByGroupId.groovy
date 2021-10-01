package contracts.membercontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'get user ids by group id'
    description 'should return status 200 and list of uuid'
    request {
        method GET()
        url($(
                consumer(regex("/api/members/group/" + uuid().toString() + "/ids")),
                producer("/api/members/group/12886ad8-f1a2-487c-a5f1-ff71d63a3b52/ids")
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
                "8f9a7cae-73c8-4ad6-b135-5bd109b51d2e"
        ])
    }
}
