package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestItem;
import com.persoff68.fatodo.builder.TestMember;
import com.persoff68.fatodo.client.CommentServiceClient;
import com.persoff68.fatodo.client.ContactServiceClient;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.client.ImageServiceClient;
import com.persoff68.fatodo.client.NotificationServiceClient;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.repository.ConfigurationRepository;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMessageVerifier
@Transactional
public abstract class ContractBase {
    private static final UUID USER_1_ID = UUID.fromString("8f9a7cae-73c8-4ad6-b135-5bd109b51d2e");
    private static final UUID USER_2_ID = UUID.fromString("4329f19c-deb7-4eaa-a841-bb46bd78f793");
    private static final UUID GROUP_1_ID = UUID.fromString("12886ad8-f1a2-487c-a5f1-ff71d63a3b52");
    private static final UUID GROUP_2_ID = UUID.fromString("605db3e3-9320-4ec9-999e-85da23c31e29");
    private static final UUID ITEM_ID = UUID.fromString("8a51fdaa-189c-4959-9016-ae79adfe0320");

    @Autowired
    WebApplicationContext context;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ConfigurationRepository configurationRepository;
    @Autowired
    EntityManager entityManager;

    @MockBean
    ImageServiceClient imageServiceClient;
    @MockBean
    ContactServiceClient contactServiceClient;
    @MockBean
    CommentServiceClient commentServiceClient;
    @MockBean
    NotificationServiceClient notificationServiceClient;
    @MockBean
    EventServiceClient eventServiceClient;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.webAppContextSetup(context);

        Group group1 = TestGroup.defaultBuilder().id(GROUP_1_ID).build().toParent();
        Group group2 = TestGroup.defaultBuilder().id(GROUP_2_ID).build().toParent();

        Member member1 = TestMember.defaultBuilder()
                .group(group1).userId(USER_1_ID).permission(Permission.ADMIN).build().toParent();
        Member member2 = TestMember.defaultBuilder()
                .group(group1).userId(USER_2_ID).permission(Permission.READ).build().toParent();
        Member member3 = TestMember.defaultBuilder()
                .group(group2).userId(USER_1_ID).permission(Permission.ADMIN).build().toParent();
        Member member4 = TestMember.defaultBuilder()
                .group(group2).userId(USER_2_ID).permission(Permission.READ).build().toParent();

        Item item1 = TestItem.defaultBuilder().group(group1).id(ITEM_ID).build().toParent();
        Item item2 = TestItem.defaultBuilder().group(group1).isArchived(true).build().toParent();

        group1.setMembers(List.of(member1, member2));
        group1.setItems(List.of(item1, item2));
        group2.setMembers(List.of(member3, member4));

        entityManager.merge(group1);
        entityManager.merge(group2);

        when(contactServiceClient.areUsersInContactList(any())).thenReturn(true);
        when(imageServiceClient.createGroupImage(any())).thenReturn("filename");
        when(imageServiceClient.updateGroupImage(any())).thenReturn("filename");
        doNothing().when(imageServiceClient).deleteGroupImage(any());
        doNothing().when(commentServiceClient).deleteAllThreadsByParentId(any());
        doNothing().when(commentServiceClient).deleteThreadByTargetId(any());
        doNothing().when(notificationServiceClient).setReminders(any(), any());
        doNothing().when(notificationServiceClient).deleteRemindersByParentId(any());
        doNothing().when(notificationServiceClient).deleteRemindersByTargetId(any());
        doNothing().when(eventServiceClient).addItemEvent(any());
        doNothing().when(eventServiceClient).deleteGroupEventsForUsers(any());
        doNothing().when(eventServiceClient).deleteGroupEvents(any());
        doNothing().when(eventServiceClient).deleteItemEvents(any());
    }

    @BeforeEach
    void cleanup() {
        configurationRepository.deleteAll();
        itemRepository.deleteAll();
        groupRepository.deleteAll();
    }

}
