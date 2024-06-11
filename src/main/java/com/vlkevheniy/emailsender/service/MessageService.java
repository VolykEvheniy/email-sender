package com.vlkevheniy.emailsender.service;

import com.vlkevheniy.emailsender.model.MessageData;
import com.vlkevheniy.emailsender.dto.MessageDto;

public interface MessageService {
    void processMessage(MessageDto messageDto);
    void sendMessage(MessageData messageData);
}
