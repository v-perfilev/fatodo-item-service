package contracts.groupresource

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'create group'
    description 'should return status 201 and GroupDTO'
    request {
        method POST()
        url("/api/groups")
        headers {
            contentType multipartFormData()
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ZjlhN2NhZS03M2M4LTRhZDYtYjEzNS01YmQxMDliNTFkMmUiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.Go0MIqfjREMHOLeqoX2Ej3DbeSG7ZxlL4UAvcxqNeO-RgrKUCrgEu77Ty1vgR_upxVGDAWZS-JfuSYPHSRtv-w")
            )
        }
        multipart(
                "title": $(
                        consumer(any()),
                        producer("test_title")
                ),
                "color": $(
                        consumer(any()),
                        producer("test_color")
                ),
        )
    }
    response {
        status 201
        headers {
            contentType applicationJson()
        }
        body(
                "title": "test_title",
                "color": "test_color",
                "users": [
                        [
                                "id"        : "8f9a7cae-73c8-4ad6-b135-5bd109b51d2e",
                                "permission": "ADMIN"
                        ]
                ]
        )
    }
}
