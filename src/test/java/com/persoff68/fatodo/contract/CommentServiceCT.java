package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.client.CommentServiceClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:commentservice:+:stubs"},
        stubsMode = StubRunnerProperties.StubsMode.REMOTE)
public class CommentServiceCT {

    @Autowired
    CommentServiceClient commentServiceClient;

    @Test
    @Disabled
    void testDeleteAllThreadsByTargetId() {
        List<UUID> targetIdList = Collections.singletonList(UUID.randomUUID());
        assertDoesNotThrow(() -> commentServiceClient.deleteAllThreadsByTargetIds(targetIdList));
    }

}
