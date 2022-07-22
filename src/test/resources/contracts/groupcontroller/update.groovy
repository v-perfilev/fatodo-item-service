package contracts.groupcontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'update group'
    description 'should return status 200 and GroupDTO'
    request {
        method PUT()
        url("/api/group")
        headers {
            contentType multipartFormData()
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ZjlhN2NhZS03M2M4LTRhZDYtYjEzNS01YmQxMDliNTFkMmUiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.Go0MIqfjREMHOLeqoX2Ej3DbeSG7ZxlL4UAvcxqNeO-RgrKUCrgEu77Ty1vgR_upxVGDAWZS-JfuSYPHSRtv-w")
            )
        }
        multipart(
                "id": $(
                        consumer(uuid()),
                        producer("12886ad8-f1a2-487c-a5f1-ff71d63a3b52")
                ),
                "title": $(
                        consumer(any()),
                        producer("test_title_new")
                ),
                "color": $(
                        consumer(any()),
                        producer("test_color_new")
                ),
        )
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body(
                "id": "12886ad8-f1a2-487c-a5f1-ff71d63a3b52",
                "title": "test_title_new",
                "color": "test_color_new",
                "members": [
                        [
                                "userId"    : "8f9a7cae-73c8-4ad6-b135-5bd109b51d2e",
                                "permission": "ADMIN"
                        ]
                ]
        )
    }
}
