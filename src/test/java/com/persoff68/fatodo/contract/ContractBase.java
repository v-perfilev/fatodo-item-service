package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestItem;
import com.persoff68.fatodo.builder.TestMember;
import com.persoff68.fatodo.client.ImageServiceClient;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMessageVerifier
public abstract class ContractBase {
    private static final UUID USER_ID = UUID.fromString("8f9a7cae-73c8-4ad6-b135-5bd109b51d2e");
    private static final UUID GROUP_1_ID = UUID.fromString("12886ad8-f1a2-487c-a5f1-ff71d63a3b52");
    private static final UUID GROUP_2_ID = UUID.fromString("605db3e3-9320-4ec9-999e-85da23c31e29");
    private static final UUID ITEM_ID = UUID.fromString("8a51fdaa-189c-4959-9016-ae79adfe0320");

    @Autowired
    WebApplicationContext context;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ItemRepository itemRepository;

    @MockBean
    ImageServiceClient imageServiceClient;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.webAppContextSetup(context);
        groupRepository.deleteAll();
        itemRepository.deleteAll();

        Member member = TestMember.defaultBuilder().id(USER_ID).permission(Permission.ADMIN).build();

        Group group1 = TestGroup.defaultBuilder().id(GROUP_1_ID).members(List.of(member)).build();
        Group group2 = TestGroup.defaultBuilder().id(GROUP_2_ID).members(List.of(member)).build();

        groupRepository.save(group1);
        groupRepository.save(group2);

        Item item = TestItem.defaultBuilder()
                .id(ITEM_ID)
                .groupId(GROUP_1_ID)
                .build();

        itemRepository.save(item);

        when(imageServiceClient.createGroupImage(any())).thenReturn("filename");
        when(imageServiceClient.updateGroupImage(any())).thenReturn("filename");
        doNothing().when(imageServiceClient).deleteGroupImage(any());
    }

}
