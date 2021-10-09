package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Primary
@RequiredArgsConstructor
public class CommentServiceClientWrapper implements CommentServiceClient {

    @Qualifier("commentServiceClient")
    private final CommentServiceClient commentServiceClient;

    @Override
    public void deleteAllThreadsByTargetIds(List<UUID> targetIdList) {
        try {
            commentServiceClient.deleteAllThreadsByTargetIds(targetIdList);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

}
