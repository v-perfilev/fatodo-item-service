package contracts.itemresource

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'get item by Id'
    description 'should return status 200 and ItemDTO'
    request {
        method GET()
        url($(
                consumer(regex("/api/items/" + uuid().toString())),
                producer("/api/items/8a51fdaa-189c-4959-9016-ae79adfe0320")
        ))
        headers {
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ZjlhN2NhZS03M2M4LTRhZDYtYjEzNS01YmQxMDliNTFkMmUiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.Go0MIqfjREMHOLeqoX2Ej3DbeSG7ZxlL4UAvcxqNeO-RgrKUCrgEu77Ty1vgR_upxVGDAWZS-JfuSYPHSRtv-w")
            )
        }
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body(
                "title": "test_value",
                "type": "TASK",
                "priority": "NORMAL",
                "description": "test_value",
                "status": "ACTIVE",
                "groupId": "ef9afb89-dc4b-4d39-b47a-199868b5de36",
        )
    }
}
