package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {

    List<Item> findAllByGroupIdIn(Set<String> groupIds);

}
