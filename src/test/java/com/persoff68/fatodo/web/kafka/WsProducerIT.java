package com.persoff68.fatodo.web.kafka;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.client.CommentServiceClient;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.client.ImageServiceClient;
import com.persoff68.fatodo.client.NotificationServiceClient;
import com.persoff68.fatodo.config.util.KafkaUtils;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.dto.WsEventDTO;
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
class WsProducerIT {

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

    private ConcurrentMessageListenerContainer<String, WsEventDTO> wsContainer;
    private BlockingQueue<ConsumerRecord<String, WsEventDTO>> wsRecords;

    @BeforeEach
    void setup() {
        when(permissionService.hasGroupsPermission(any(), any(), any())).thenReturn(true);
        when(permissionService.hasItemsPermission(any(), any(), any())).thenReturn(true);

        startWsConsumer();
    }

    @AfterEach
    void cleanup() {
        groupRepository.deleteAll();

        stopWsConsumer();
    }

    @Test
    @WithCustomSecurityContext
    void testSendEvent_ok() throws Exception {
        Group newGroup = TestGroup.defaultBuilder().build().toParent();
        groupService.create(newGroup, null);

        ConsumerRecord<String, WsEventDTO> record = wsRecords.poll(5, TimeUnit.SECONDS);

        assertThat(eventServiceClient).isInstanceOf(EventProducer.class);
        assertThat(record).isNotNull();
        verify(eventServiceClient).addItemEvent(any());
    }

    private void startWsConsumer() {
        JavaType javaType = objectMapper.getTypeFactory().constructType(WsEventDTO.class);
        ConcurrentKafkaListenerContainerFactory<String, WsEventDTO> stringContainerFactory =
                KafkaUtils.buildJsonContainerFactory(embeddedKafkaBroker.getBrokersAsString(),
                        "test", "earliest", javaType);
        wsContainer = stringContainerFactory.createContainer("event_add");
        wsRecords = new LinkedBlockingQueue<>();
        wsContainer.setupMessageListener((MessageListener<String, WsEventDTO>) wsRecords::add);
        wsContainer.start();
        ContainerTestUtils.waitForAssignment(wsContainer, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    private void stopWsConsumer() {
        wsContainer.stop();
    }

}
