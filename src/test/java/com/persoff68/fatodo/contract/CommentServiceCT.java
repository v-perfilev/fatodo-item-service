package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.client.CommentServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:commentservice:+:stubs"},
        stubsMode = StubRunnerProperties.StubsMode.REMOTE)
class CommentServiceCT {

    @Autowired
    CommentServiceClient commentServiceClient;

    @Test
    void testDeleteAllThreadsByParentId() {
        UUID parentId = UUID.randomUUID();
        assertDoesNotThrow(() -> commentServiceClient.deleteAllThreadsByParentId(parentId));
    }

    @Test
    void testDeleteThreadByTargetId() {
        UUID targetId = UUID.randomUUID();
        assertDoesNotThrow(() -> commentServiceClient.deleteThreadByTargetId(targetId));
    }

}
