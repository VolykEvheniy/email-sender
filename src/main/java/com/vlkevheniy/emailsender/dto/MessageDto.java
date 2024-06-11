package com.vlkevheniy.emailsender.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {
    private String to;
    private String subject;
    private String content;
}
