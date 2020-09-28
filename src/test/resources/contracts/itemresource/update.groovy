package contracts.itemresource

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'update item'
    description 'should return status 200 and ItemDTO'
    request {
        method PUT()
        url("/api/items")
        headers {
            contentType applicationJson()
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0X3VzZXIiLCJhdXRob3JpdGllcyI6IlJPTEVfVVNFUiIsImlhdCI6MCwiZXhwIjozMjUwMzY3NjQwMH0.ggV38p_Fnqo2OZNtwR3NWKZhMXPd-vf4PrRxN0NmTWsHPrKwWZJSGO2dJBBPWXWs4OI6tjsNV2TM3Kf6NK92hw")
            )
        }
        body(
                "id": $(
                        consumer(any()),
                        producer("test_id_1")
                ),
                "title": $(
                        consumer(any()),
                        producer("test_title_new")
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
                "status": $(
                        consumer(any()),
                        producer("ACTIVE")
                ),
                "groupId": $(
                        consumer(any()),
                        producer("test_group_id")
                )
        )
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body(
                "id": "test_id_1",
                "title": "test_title_new",
                "type": "TASK",
                "priority": "NORMAL",
                "description": "test_body",
                "status": "ACTIVE",
                "groupId": "test_group_id",
        )
    }
}
