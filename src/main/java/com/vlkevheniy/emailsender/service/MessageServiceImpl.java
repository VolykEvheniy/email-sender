package com.vlkevheniy.emailsender.service;

import com.vlkevheniy.emailsender.model.MessageData;
import com.vlkevheniy.emailsender.model.MessageStatus;
import com.vlkevheniy.emailsender.dto.MessageDto;
import com.vlkevheniy.emailsender.exception.EmailSendingException;
import com.vlkevheniy.emailsender.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{

    private final MessageRepository messageRepository;
    private final EmailService emailService;

    public void processMessage(MessageDto messageDto) {
        MessageData messageData = new MessageData();
        messageData.setTo(messageDto.getTo());
        messageData.setSubject(messageDto.getSubject());
        messageData.setContent(messageData.getContent());
        messageData.setStatus(MessageStatus.FAILED);
        messageData.setAttemptCount(0);
        messageData.setLastUpdateTime(Instant.now());

        messageRepository.save(messageData);
        sendMessage(messageData);
    }

    public void sendMessage(MessageData messageData) {
        try {
            emailService.sendEmail(messageData.getTo(), messageData.getSubject(), messageData.getContent());
            messageData.setStatus(MessageStatus.SENT);
        } catch (EmailSendingException e) {
            messageData.setStatus(MessageStatus.FAILED);
            messageData.setErrorMessage(e.getClass().getName() + ": " + e.getMessage());
            messageData.setAttemptCount(messageData.getAttemptCount() + 1);
        }
        messageData.setLastUpdateTime(Instant.now());
        messageRepository.save(messageData);
    }
}
