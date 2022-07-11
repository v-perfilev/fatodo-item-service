package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommentServiceClientWrapper implements CommentServiceClient {

    @Qualifier("feignCommentServiceClient")
    private final CommentServiceClient commentServiceClient;

    @Override
    public void deleteAllThreadsByParentId(UUID parentId) {
        try {
            commentServiceClient.deleteAllThreadsByParentId(parentId);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void deleteThreadByTargetId(UUID targetId) {
        try {
            commentServiceClient.deleteThreadByTargetId(targetId);
        } catch (Exception e) {
            throw new ClientException();
        }
    }
}
