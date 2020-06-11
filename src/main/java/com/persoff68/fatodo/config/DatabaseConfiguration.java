package com.persoff68.fatodo.config;

import com.github.cloudyrock.spring.v5.MongockConfiguration;
import com.github.cloudyrock.spring.v5.MongockContext;
import com.github.cloudyrock.spring.v5.MongockSpring5;
import com.persoff68.fatodo.config.constant.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableMongoRepositories(basePackages = AppConstants.REPOSITORY_PATH)
@EnableMongoAuditing(auditorAwareRef = "securityAuditorAware")
@RequiredArgsConstructor
public class DatabaseConfiguration {

    private final ApplicationContext context;
    private final MongoTemplate mongoTemplate;
    private final MongoConverter mongoConverter;

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener() {
        return new ValidatingMongoEventListener(validator());
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initIndicesAfterStartup() {
        Object mappingContext = this.mongoConverter.getMappingContext();
        if (mappingContext instanceof MongoMappingContext) {
            MongoMappingContext mongoMappingContext = (MongoMappingContext) mappingContext;
            for (BasicMongoPersistentEntity<?> persistentEntity : mongoMappingContext.getPersistentEntities()) {
                Class<?> clazz = persistentEntity.getType();
                if (clazz.isAnnotationPresent(Document.class)) {
                    IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
                    IndexOperations indexOps = mongoTemplate.indexOps(clazz);
                    resolver.resolveIndexFor(clazz).forEach(indexOps::ensureIndex);
                }
            }
        }
    }

    @Bean
    public MongockSpring5.MongockApplicationRunner mongock() {
        MongockConfiguration mongockConfiguration = new MongockConfiguration();
        mongockConfiguration.setChangeLogsScanPackage(AppConstants.MIGRATION_PATH);
        return new MongockContext().mongockApplicationRunner(context, mongoTemplate, mongockConfiguration);
    }

}
