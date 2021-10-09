package com.persoff68.fatodo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "comment-service", primary = false)
public interface CommentServiceClient {

    @PostMapping(value = "/api/threads/delete",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    void deleteAllThreadsByTargetIds(List<UUID> targetIdList);

}
