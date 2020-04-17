package com.persoff68.fatodo.config.database;

import com.persoff68.fatodo.model.AbstractAuditingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MongoEventListener<E> extends AbstractMongoEventListener<E> {

    private final MongoTemplate mongoTemplate;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<E> event) {
        E source = event.getSource();
        if (source instanceof AbstractAuditingModel) {
            onBeforeAuditingConvert(source);
        }
    }

    private void onBeforeAuditingConvert(E source) {
        AbstractAuditingModel sourceModel = (AbstractAuditingModel) source;
        String id = sourceModel.getId();
        if (id != null) {
            AbstractAuditingModel dbModel = (AbstractAuditingModel) mongoTemplate.findById(id, source.getClass());
            if (dbModel != null) {
                sourceModel.setCreatedBy(dbModel.getCreatedBy());
                sourceModel.setCreatedDate(dbModel.getCreatedDate());
                sourceModel.setLastModifiedBy(dbModel.getLastModifiedBy());
                sourceModel.setLastModifiedDate(dbModel.getLastModifiedDate());
            }
        }
    }

}
