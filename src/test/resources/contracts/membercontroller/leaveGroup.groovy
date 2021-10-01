package contracts.membercontroller

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'leave group'
    description 'should return status 200'
    request {
        method GET()
        url($(
                consumer(regex("/api/members/group/" + uuid().toString() + "/leave")),
                producer("/api/members/group/12886ad8-f1a2-487c-a5f1-ff71d63a3b52/leave")
        ))
        headers {
            header 'Authorization': $(
                    consumer(containing("Bearer")),
                    producer("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI0MzI5ZjE5Yy1kZWI3LTRlYWEtYTg0MS1iYjQ2YmQ3OGY3OTMiLCJ1c2VybmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiaWF0IjowLCJleHAiOjMyNTAzNjc2NDAwfQ.fCqSRDlcM0nuGxeZ7wgd9wJ-TefWrfLueq9PWENCV2zd4z1IZz7oiyZnf1gFET-eUfIFD1-Ny0nl8K-A99UdXQ")
            )
        }
    }
    response {
        status 200
    }
}
