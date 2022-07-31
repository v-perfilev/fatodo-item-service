package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.builder.TestClearEventDTO;
import com.persoff68.fatodo.builder.TestWsEventDTO;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.model.dto.ClearEventDTO;
import com.persoff68.fatodo.model.dto.WsEventDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:wsservice:+:stubs"}, stubsMode =
        StubRunnerProperties.StubsMode.REMOTE)
class WsServiceCT {

    @Autowired
    WsServiceClient wsServiceClient;

    @Test
    void testSendClearEvent() {
        ClearEventDTO clearEventDTO = TestClearEventDTO.defaultBuilder().build().toParent();
        WsEventDTO<ClearEventDTO> dto =
                TestWsEventDTO.<ClearEventDTO>defaultBuilder().content(clearEventDTO).build().toParent();
        assertDoesNotThrow(() -> wsServiceClient.sendClearEvent(dto));
    }

}
