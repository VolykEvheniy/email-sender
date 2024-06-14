package com.vlkevheniy.emailsender.listener;


import com.vlkevheniy.common.dto.MessageDto;
import com.vlkevheniy.emailsender.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageListener {

    private final MessageService messageService;

    @KafkaListener(topics = "${kafka.topic.messageReceived}", groupId = "${spring.kafka.consumer.group-id}")
    public void messageReceived(MessageDto message) {
        messageService.processMessage(message);
    }
}
