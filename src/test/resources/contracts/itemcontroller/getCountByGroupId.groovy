package contracts.itemresource

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'get items count by groupId'
    description 'should return status 200 and int'
    request {
        method GET()
        url($(
                consumer(regex('\\/api\\/item\\/count\\/group\\/[\\w-]+')),
                producer("/api/item/count/group/test_group_id")
        ))
        headers {
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzIiwidXNlcm5hbWUiOiJ0ZXN0X3VzZXIiLCJhdXRob3JpdGllcyI6IlJPTEVfVVNFUiIsImlhdCI6MCwiZXhwIjozMjUwMzY3NjQwMH0.ggV38p_Fnqo2OZNtwR3NWKZhMXPd-vf4PrRxN0NmTWsHPrKwWZJSGO2dJBBPWXWs4OI6tjsNV2TM3Kf6NK92hw")
            )
        }
    }
    response {
        status 200
        body(
                "1"
        )
    }
}
