package com.persoff68.fatodo.config;

import com.persoff68.fatodo.client.CommentServiceClient;
import com.persoff68.fatodo.client.ContactServiceClient;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.client.ImageServiceClient;
import com.persoff68.fatodo.client.NotificationServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class ClientConfiguration {

    private final BeanFactory beanFactory;

    @Bean
    @Primary
    public CommentServiceClient commentClient() {
        return (CommentServiceClient) beanFactory.getBean("commentServiceClientWrapper");
    }

    @Bean
    @Primary
    public ContactServiceClient contactClient() {
        return (ContactServiceClient) beanFactory.getBean("contactServiceClientWrapper");
    }

    @Bean
    @Primary
    public EventServiceClient eventClient() {
        boolean kafkaProducerExists = beanFactory.containsBean("eventProducer");
        return kafkaProducerExists
                ? (EventServiceClient) beanFactory.getBean("eventProducer")
                : (EventServiceClient) beanFactory.getBean("eventServiceClientWrapper");
    }

    @Bean
    @Primary
    public ImageServiceClient imageClient() {
        return (ImageServiceClient) beanFactory.getBean("imageServiceClientWrapper");
    }

    @Bean
    @Primary
    public NotificationServiceClient notificationClient() {
        return (NotificationServiceClient) beanFactory.getBean("notificationServiceClientWrapper");
    }

    @Bean
    @Primary
    public WsServiceClient wsClient() {
        boolean kafkaProducerExists = beanFactory.containsBean("wsProducer");
        return kafkaProducerExists
                ? (WsServiceClient) beanFactory.getBean("wsProducer")
                : (WsServiceClient) beanFactory.getBean("wsServiceClientWrapper");
    }

}
