package contracts.itemresource

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'get all items for user'
    description 'should return status 200 and list of UserDTOs'
    request {
        method GET()
        url("/api/items")
        headers {
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0X3VzZXIiLCJhdXRob3JpdGllcyI6IlJPTEVfVVNFUiIsImlhdCI6MCwiZXhwIjozMjUwMzY3NjQwMH0.ggV38p_Fnqo2OZNtwR3NWKZhMXPd-vf4PrRxN0NmTWsHPrKwWZJSGO2dJBBPWXWs4OI6tjsNV2TM3Kf6NK92hw")
            )
        }
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body([
                [
                        "id"         : "test_id_1",
                        "title"      : "test_title_1",
                        "body"       : "test_body_1",
                        "status"     : "ACTIVE",
                        "groupId"    : "test_group_id"
                ]
        ])
    }
}
