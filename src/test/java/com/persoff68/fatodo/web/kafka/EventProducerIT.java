package com.persoff68.fatodo.web.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestItem;
import com.persoff68.fatodo.builder.TestMember;
import com.persoff68.fatodo.client.CommentServiceClient;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.client.ImageServiceClient;
import com.persoff68.fatodo.client.NotificationServiceClient;
import com.persoff68.fatodo.config.util.KafkaUtils;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.service.GroupService;
import com.persoff68.fatodo.service.ItemService;
import com.persoff68.fatodo.service.MemberService;
import com.persoff68.fatodo.service.client.PermissionService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "kafka.bootstrapAddress=localhost:9092",
        "kafka.groupId=test",
        "kafka.partitions=1",
        "kafka.autoOffsetResetConfig=earliest"
})
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class EventProducerIT {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    GroupService groupService;
    @Autowired
    ItemService itemService;
    @Autowired
    MemberService memberService;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    GroupRepository groupRepository;

    @MockBean
    ImageServiceClient imageServiceClient;
    @MockBean
    CommentServiceClient commentServiceClient;
    @MockBean
    NotificationServiceClient notificationServiceClient;
    @MockBean
    PermissionService permissionService;
    @SpyBean
    EventServiceClient eventServiceClient;

    private ConcurrentMessageListenerContainer<String, String> eventAddContainer;
    private BlockingQueue<ConsumerRecord<String, String>> eventAddRecords;

    private ConcurrentMessageListenerContainer<String, String> eventDeleteContainer;
    private BlockingQueue<ConsumerRecord<String, String>> eventDeleteRecords;

    Group group;
    Item item;
    Member member1;
    Member member2;


    @BeforeEach
    void setup() {
        group = TestGroup.defaultBuilder().build().toParent();
        member1 = TestMember.defaultBuilder().group(group).permission(Permission.ADMIN).build().toParent();
        member2 = TestMember.defaultBuilder().group(group).build().toParent();
        item = TestItem.defaultBuilder().group(group).build().toParent();
        group.setMembers(List.of(member1, member2));
        group.setItems(List.of(item));
        group = groupRepository.save(group);
        item = group.getItems().get(0);
        member1 = group.getMembers().get(0);
        member2 = group.getMembers().get(1);

        when(permissionService.hasGroupsPermission(any(), any(), any())).thenReturn(true);
        when(permissionService.hasItemsPermission(any(), any(), any())).thenReturn(true);

        startEventAddConsumer();
        startEventDeleteConsumer();
    }

    @AfterEach
    void cleanup() {
        groupRepository.deleteAll();

        stopEventAddConsumer();
        stopEventDeleteConsumer();
    }

    @Test
    @WithCustomSecurityContext
    void testSendItemEvent_ok() throws Exception {
        Group newGroup = TestGroup.defaultBuilder().build().toParent();
        groupService.create(newGroup, null);

        ConsumerRecord<String, String> record = eventAddRecords.poll(5, TimeUnit.SECONDS);

        assertThat(eventServiceClient).isInstanceOf(EventProducer.class);
        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo("item");
        verify(eventServiceClient).addItemEvent(any());
    }

    @Test
    void testSendDeleteGroupEvents_ok() throws Exception {
        groupService.delete(member1.getUserId(), group.getId());

        ConsumerRecord<String, String> record = eventDeleteRecords.poll(5, TimeUnit.SECONDS);

        assertThat(eventServiceClient).isInstanceOf(EventProducer.class);
        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo("group-delete");
        verify(eventServiceClient).deleteGroupEvents(any());
    }

    @Test
    void testSendDeleteItemEvents_ok() throws Exception {
        itemService.delete(member1.getUserId(), item.getId());

        ConsumerRecord<String, String> record = eventDeleteRecords.poll(5, TimeUnit.SECONDS);

        assertThat(eventServiceClient).isInstanceOf(EventProducer.class);
        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo("item-delete");
        verify(eventServiceClient).deleteItemEvents(any());
    }

    @Test
    void testSendDeleteGroupEventsForUsers_ok() throws Exception {
        memberService.leaveGroup(member2.getUserId(), group.getId());

        ConsumerRecord<String, String> record = eventDeleteRecords.poll(5, TimeUnit.SECONDS);

        assertThat(eventServiceClient).isInstanceOf(EventProducer.class);
        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo("group-delete-users");
        verify(eventServiceClient).deleteGroupEventsForUsers(any());
    }


    private void startEventAddConsumer() {
        ConcurrentKafkaListenerContainerFactory<String, String> stringContainerFactory =
                KafkaUtils.buildStringContainerFactory(embeddedKafkaBroker.getBrokersAsString(), "test", "earliest");
        eventAddContainer = stringContainerFactory.createContainer("event_add");
        eventAddRecords = new LinkedBlockingQueue<>();
        eventAddContainer.setupMessageListener((MessageListener<String, String>) eventAddRecords::add);
        eventAddContainer.start();
        ContainerTestUtils.waitForAssignment(eventAddContainer, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    private void stopEventAddConsumer() {
        eventAddContainer.stop();
    }

    private void startEventDeleteConsumer() {
        ConcurrentKafkaListenerContainerFactory<String, String> stringContainerFactory =
                KafkaUtils.buildStringContainerFactory(embeddedKafkaBroker.getBrokersAsString(), "test", "earliest");
        eventDeleteContainer = stringContainerFactory.createContainer("event_delete");
        eventDeleteRecords = new LinkedBlockingQueue<>();
        eventDeleteContainer.setupMessageListener((MessageListener<String, String>) eventDeleteRecords::add);
        eventDeleteContainer.start();
        ContainerTestUtils.waitForAssignment(eventDeleteContainer, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    private void stopEventDeleteConsumer() {
        eventDeleteContainer.stop();
    }

}
