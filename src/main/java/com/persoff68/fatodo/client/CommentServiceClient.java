package com.persoff68.fatodo.client;

import com.persoff68.fatodo.client.configuration.FeignAuthConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "comment-service", primary = false,
        configuration = {FeignAuthConfiguration.class},
        qualifiers = {"feignCommentServiceClient"})
public interface CommentServiceClient {

    @DeleteMapping(value = "/api/thread/{parentId}/parent")
    void deleteAllThreadsByParentId(@PathVariable UUID parentId);

    @DeleteMapping(value = "/api/thread/{targetId}/target")
    void deleteThreadByTargetId(@PathVariable UUID targetId);

}
