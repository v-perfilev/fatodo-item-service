package contracts.itemresource

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'delete item'
    description 'should return status 200'
    request {
        method DELETE()
        url($(
                consumer(regex('\\/api\\/items\\/[\\w-]+')),
                producer("/api/items/test_id_1")
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
    }
}
