package contracts.itemcontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'create item'
    description 'should return status 201 and ItemDTO'
    request {
        method POST()
        url("/api/item")
        headers {
            contentType applicationJson()
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ZjlhN2NhZS03M2M4LTRhZDYtYjEzNS01YmQxMDliNTFkMmUiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.Go0MIqfjREMHOLeqoX2Ej3DbeSG7ZxlL4UAvcxqNeO-RgrKUCrgEu77Ty1vgR_upxVGDAWZS-JfuSYPHSRtv-w")
            )
        }
        body(
                "title": $(
                        consumer(any()),
                        producer("test_title")
                ),
                "status": $(
                        consumer(any()),
                        producer("CREATED")
                ),
                "type": $(
                        consumer(any()),
                        producer("TASK")
                ),
                "priority": $(
                        consumer(any()),
                        producer("NORMAL")
                ),
                "description": $(
                        consumer(any()),
                        producer("test_body")
                ),
                "groupId": $(
                        consumer(any()),
                        producer("12886ad8-f1a2-487c-a5f1-ff71d63a3b52")
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
                "status": "CREATED",
                "type": "TASK",
                "priority": "NORMAL",
                "description": "test_body",
                "remindersCount": 0,
                "groupId": "12886ad8-f1a2-487c-a5f1-ff71d63a3b52",
        )
    }
}
