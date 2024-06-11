package com.vlkevheniy.emailsender.scheduler;

import com.vlkevheniy.emailsender.model.MessageData;
import com.vlkevheniy.emailsender.model.MessageStatus;
import com.vlkevheniy.emailsender.repository.MessageRepository;
import com.vlkevheniy.emailsender.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class MessageScheduler {

    private final MessageService messageService;
    private final MessageRepository messageRepository;

    @Scheduled(fixedRate = 300000)
    public void retryFailedMessages() {
        List<MessageData> failedMessages = messageRepository.findByStatus(MessageStatus.FAILED);
        for (MessageData message : failedMessages) {
            messageService.sendMessage(message);
        }
    }
}
