package contracts.itemcontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'get archived items by groupId'
    description 'should return status 200 and list of ItemDTOs'
    request {
        method GET()
        url($(
                consumer(regex("/api/item/" + uuid().toString() + "/group/archived")),
                producer("/api/item/12886ad8-f1a2-487c-a5f1-ff71d63a3b52/group/archived")
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
                        "priority"   : 2,
                        "description": "test_value",
                        "groupId"    : "12886ad8-f1a2-487c-a5f1-ff71d63a3b52",
                        "done"       : false,
                        "archived"   : true
                ],
                "count": 1
        ])
    }
}
