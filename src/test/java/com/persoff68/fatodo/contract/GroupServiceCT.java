package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.client.GroupServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:groupservice:+:stubs:6565"},
        stubsMode = StubRunnerProperties.StubsMode.REMOTE)
public class GroupServiceCT {

    @Autowired
    GroupServiceClient groupServiceClient;

    @Test
    void testCanRead() {
        boolean result = groupServiceClient.canRead(List.of("test_group_id"));
        assertThat(result).isTrue();
    }

    @Test
    void testCanEdit() {
        boolean result = groupServiceClient.canEdit(List.of("test_group_id"));
        assertThat(result).isTrue();
    }

    @Test
    void testCanAdmin() {
        boolean result = groupServiceClient.canAdmin(List.of("test_group_id"));
        assertThat(result).isTrue();
    }

    @Test
    void testGetGroupIdsForUser() {
        List<String> result = groupServiceClient.getAllGroupIdsForUser();
        assertThat(result).isNotEmpty();
    }

}
