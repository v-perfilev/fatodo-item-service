package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.client.GroupServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:groupservice:+:stubs"},
        stubsMode = StubRunnerProperties.StubsMode.REMOTE)
public class GroupServiceCT {

    @Autowired
    GroupServiceClient groupServiceClient;

    @Test
    void testCanRead() {
        boolean result = groupServiceClient.canRead(List.of(UUID.randomUUID()));
        assertThat(result).isTrue();
    }

    @Test
    void testCanEdit() {
        boolean result = groupServiceClient.canEdit(List.of(UUID.randomUUID()));
        assertThat(result).isTrue();
    }

    @Test
    void testCanAdmin() {
        boolean result = groupServiceClient.canAdmin(List.of(UUID.randomUUID()));
        assertThat(result).isTrue();
    }

}
