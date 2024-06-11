package com.vlkevheniy.emailsender.repository;

import com.vlkevheniy.emailsender.model.MessageData;
import com.vlkevheniy.emailsender.model.MessageStatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MessageRepository extends ElasticsearchRepository<MessageData, String> {
    List<MessageData> findByStatus(MessageStatus status);
}
