package contracts.itemresource

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'get archived items by groupId'
    description 'should return status 200 and list of ItemDTOs'
    request {
        method GET()
        url($(
                consumer(regex("/api/items/archived/" + uuid().toString() + "/group-id")),
                producer("/api/items/archived/12886ad8-f1a2-487c-a5f1-ff71d63a3b52/group-id")
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
        body([
                "data" : [
                        "title"      : "test_value",
                        "type"       : "TASK",
                        "priority"   : "NORMAL",
                        "description": "test_value",
                        "status"     : "CREATED",
                        "groupId"    : "12886ad8-f1a2-487c-a5f1-ff71d63a3b52",
                        "archived" : true
                ],
                "count": 1
        ])
    }
}