package com.vlkevheniy.emailsender.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Getter
@Setter
@Document(indexName="messages")
public class MessageData {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String to;

    @Field(type = FieldType.Text)
    private String subject;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword)
    private MessageStatus status;

    @Field(type = FieldType.Text)
    private String errorMessage;

    @Field(type = FieldType.Integer)
    private int attemptCount;

    @Field(type = FieldType.Date)
    private Instant lastUpdateTime;

}
