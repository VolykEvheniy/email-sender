package com.vlkevheniy.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Jacksonized
public class MessageDto {
    private String to;
    private String subject;
    private String content;
}
