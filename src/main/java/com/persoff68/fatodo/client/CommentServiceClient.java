package com.persoff68.fatodo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "comment-service", primary = false, qualifiers = {"feignCommentServiceClient"})
public interface CommentServiceClient {

    @DeleteMapping(value = "/api/threads/{parentId}/parent")
    void deleteAllThreadsByParentId(@PathVariable UUID parentId);

    @DeleteMapping(value = "/api/threads/{targetId}/target")
    void deleteThreadByTargetId(@PathVariable UUID targetId);

}
