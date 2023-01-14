package contracts.itemcontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'update item'
    description 'should return status 200 and ItemDTO'
    request {
        method PUT()
        url("/api/item")
        headers {
            contentType applicationJson()
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ZjlhN2NhZS03M2M4LTRhZDYtYjEzNS01YmQxMDliNTFkMmUiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.Go0MIqfjREMHOLeqoX2Ej3DbeSG7ZxlL4UAvcxqNeO-RgrKUCrgEu77Ty1vgR_upxVGDAWZS-JfuSYPHSRtv-w")
            )
        }
        body(
                "id": $(
                        consumer(any()),
                        producer("8a51fdaa-189c-4959-9016-ae79adfe0320")
                ),
                "title": $(
                        consumer(any()),
                        producer("test_title_new")
                ),
                "priority": $(
                        consumer(anyNumber()),
                        producer(2)
                ),
                "description": $(
                        consumer(any()),
                        producer("test_body")
                ),
                "groupId": $(
                        consumer(any()),
                        producer("ef9afb89-dc4b-4d39-b47a-199868b5de36")
                )
        )
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body(
                "id": "8a51fdaa-189c-4959-9016-ae79adfe0320",
                "title": "test_title_new",
                "priority": 2,
                "description": "test_body",
                "remindersCount": 0,
                "groupId": "12886ad8-f1a2-487c-a5f1-ff71d63a3b52",
                "done": false,
                "archived": false,
        )
    }
}
