package contracts.infocontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'get reminder info by item id'
    description 'should return status 200 and ReminderInfoDTO'
    request {
        method GET()
        url($(
                consumer(regex("/api/info/item-reminder/" + uuid().toString())),
                producer("/api/info/item-reminder/8a51fdaa-189c-4959-9016-ae79adfe0320")
        ))
        headers {
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDAiLCJ1c2VybmFtZSI6InRlc3Rfc3lzdGVtIiwiYXV0aG9yaXRpZXMiOiJST0xFX1NZU1RFTSIsImlhdCI6MCwiZXhwIjozMjUwMzY3NjQwMH0.roNFKrM7NjEzXvRFRHlJXw0YxSFZ-4Afqvn7eFatpGF14olhXBvCvR9CkPkmlnlCAOYbpDO18krfi6SEX0tQ6Q")
            )
        }
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body(
                "groupId": "12886ad8-f1a2-487c-a5f1-ff71d63a3b52",
                "itemId": "8a51fdaa-189c-4959-9016-ae79adfe0320",
                "message": "test_value",
                "url": "/items/8a51fdaa-189c-4959-9016-ae79adfe0320",
        )
    }
}
