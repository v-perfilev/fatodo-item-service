package contracts.remindercontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'get reminder message by item id'
    description 'should return status 200 and ReminderMessageDTO'
    request {
        method GET()
        url($(
                consumer(regex("/api/reminders/item/" + uuid().toString())),
                producer("/api/reminders/item/8a51fdaa-189c-4959-9016-ae79adfe0320")
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
                "message": "test_value",
                "url": "/items/8a51fdaa-189c-4959-9016-ae79adfe0320",
        )
    }
}
