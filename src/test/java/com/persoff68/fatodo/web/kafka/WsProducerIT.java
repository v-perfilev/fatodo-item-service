package com.persoff68.fatodo.web.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestItem;
import com.persoff68.fatodo.builder.TestMember;
import com.persoff68.fatodo.client.CommentServiceClient;
import com.persoff68.fatodo.client.ImageServiceClient;
import com.persoff68.fatodo.client.NotificationServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.config.util.KafkaUtils;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.service.GroupService;
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

@SpringBootTest(properties = {
        "kafka.bootstrapAddress=localhost:9092",
        "kafka.groupId=test",
        "kafka.partitions=1",
        "kafka.autoOffsetResetConfig=earliest"
})
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class WsProducerIT {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    GroupService groupService;
    @Autowired
    GroupRepository groupRepository;

    @MockBean
    CommentServiceClient commentServiceClient;
    @MockBean
    NotificationServiceClient notificationServiceClient;
    @MockBean
    ImageServiceClient imageServiceClient;
    @SpyBean
    WsServiceClient wsServiceClient;

    private ConcurrentMessageListenerContainer<String, String> wsClearContainer;
    private BlockingQueue<ConsumerRecord<String, String>> wsClearRecords;

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

        startWsClearConsumer();
    }

    @AfterEach
    void cleanup() {
        stopWsClearConsumer();
    }

    @Test
    void testSendClearEvent() throws Exception {
        groupService.delete(member1.getUserId(), group.getId());

        ConsumerRecord<String, String> record = wsClearRecords.poll(10, TimeUnit.SECONDS);

        assertThat(wsServiceClient).isInstanceOf(WsProducer.class);
        assertThat(record).isNotNull();
        verify(wsServiceClient).sendClearEvent(any());
    }

    private void startWsClearConsumer() {
        ConcurrentKafkaListenerContainerFactory<String, String> stringContainerFactory =
                KafkaUtils.buildStringContainerFactory(embeddedKafkaBroker.getBrokersAsString(), "test", "earliest");
        wsClearContainer = stringContainerFactory.createContainer("ws_clear");
        wsClearRecords = new LinkedBlockingQueue<>();
        wsClearContainer.setupMessageListener((MessageListener<String, String>) wsClearRecords::add);
        wsClearContainer.start();
        ContainerTestUtils.waitForAssignment(wsClearContainer, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    private void stopWsClearConsumer() {
        wsClearContainer.stop();
    }

}
