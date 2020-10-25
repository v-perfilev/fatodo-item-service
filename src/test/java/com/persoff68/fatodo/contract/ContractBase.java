package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.builder.TestItem;
import com.persoff68.fatodo.client.GroupServiceClient;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.repository.ItemRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMessageVerifier
public abstract class ContractBase {

    private static final UUID ITEM_ID = UUID.fromString("8a51fdaa-189c-4959-9016-ae79adfe0320");
    private static final UUID GROUP_ID = UUID.fromString("ef9afb89-dc4b-4d39-b47a-199868b5de36");

    @Autowired
    WebApplicationContext context;
    @Autowired
    ItemRepository itemRepository;

    @MockBean
    GroupServiceClient groupServiceClient;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.webAppContextSetup(context);

        Item item = TestItem.defaultBuilder()
                .id(ITEM_ID)
                .groupId(GROUP_ID)
                .build();

        itemRepository.deleteAll();
        itemRepository.save(item);

        when(groupServiceClient.canRead(any())).thenReturn(true);
        when(groupServiceClient.canEdit(any())).thenReturn(true);
        when(groupServiceClient.canAdmin(any())).thenReturn(true);
    }

}
