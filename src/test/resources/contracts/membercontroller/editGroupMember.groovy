package contracts.membercontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'edit group member'
    description 'should return status 200'
    request {
        method POST()
        url($(
                consumer(regex("/api/members/group/" + uuid().toString() + "/remove")),
                producer("/api/members/group/12886ad8-f1a2-487c-a5f1-ff71d63a3b52/remove")
        ))
        headers {
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ZjlhN2NhZS03M2M4LTRhZDYtYjEzNS01YmQxMDliNTFkMmUiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.Go0MIqfjREMHOLeqoX2Ej3DbeSG7ZxlL4UAvcxqNeO-RgrKUCrgEu77Ty1vgR_upxVGDAWZS-JfuSYPHSRtv-w")
            )
        }
        body(
                "id": $(
                        consumer(any()),
                        producer("4329f19c-deb7-4eaa-a841-bb46bd78f793")
                ),
                "permission": $(
                        consumer(any()),
                        producer("EDIT")
                ),
        )
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
    }
}
