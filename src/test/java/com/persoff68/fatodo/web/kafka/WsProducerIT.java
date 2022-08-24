package com.persoff68.fatodo.web.kafka;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.config.util.KafkaUtils;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.dto.event.WsEventDTO;
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
    GroupService groupService;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    GroupRepository groupRepository;

    @MockBean
    EventServiceClient eventServiceClient;
    @SpyBean
    WsServiceClient wsServiceClient;

    private ConcurrentMessageListenerContainer<String, WsEventDTO> wsContainer;
    private BlockingQueue<ConsumerRecord<String, WsEventDTO>> wsRecords;

    @BeforeEach
    void setup() {
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

        assertThat(wsServiceClient).isInstanceOf(WsProducer.class);
        assertThat(record).isNotNull();
        verify(wsServiceClient).sendEvent(any());
    }

    private void startWsConsumer() {
        JavaType javaType = objectMapper.getTypeFactory().constructType(WsEventDTO.class);
        ConcurrentKafkaListenerContainerFactory<String, WsEventDTO> containerFactory =
                KafkaUtils.buildJsonContainerFactory(embeddedKafkaBroker.getBrokersAsString(),
                        "test", "earliest", javaType);
        wsContainer = containerFactory.createContainer("ws");
        wsRecords = new LinkedBlockingQueue<>();
        wsContainer.setupMessageListener((MessageListener<String, WsEventDTO>) wsRecords::add);
        wsContainer.start();
        ContainerTestUtils.waitForAssignment(wsContainer, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    private void stopWsConsumer() {
        wsContainer.stop();
    }

}
