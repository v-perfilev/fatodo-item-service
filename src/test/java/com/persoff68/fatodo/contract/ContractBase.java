package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.FactoryUtils;
import com.persoff68.fatodo.client.GroupServiceClient;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.repository.ItemRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMessageVerifier
public abstract class ContractBase {

    @Autowired
    WebApplicationContext context;
    @Autowired
    ItemRepository itemRepository;

    @MockBean
    GroupServiceClient groupServiceClient;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.webAppContextSetup(context);
        itemRepository.deleteAll();
        Item item = FactoryUtils.createItem("1", "test_group_id", ItemStatus.ACTIVE);
        item.setId("test_id_1");
        itemRepository.save(item);

        when(groupServiceClient.getAllGroupIdsForUser()).thenReturn(List.of("test_group_id"));
        when(groupServiceClient.canRead(any())).thenReturn(true);
        when(groupServiceClient.canEdit(any())).thenReturn(true);
        when(groupServiceClient.canAdmin(any())).thenReturn(true);
    }

}
