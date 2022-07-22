package contracts.membercontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'remove members from group'
    description 'should return status 200'
    request {
        method DELETE()
        url($(
                consumer(regex("/api/member/" + uuid().toString() + "\\?ids=.*")),
                producer("/api/member/12886ad8-f1a2-487c-a5f1-ff71d63a3b52?ids=4329f19c-deb7-4eaa-a841-bb46bd78f793")
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
    }
}
