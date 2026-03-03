package com.eventtracker.consumer.repository;

import com.eventtracker.consumer.model.ActivityEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityEventRepository extends MongoRepository<ActivityEvent, String> {

}
