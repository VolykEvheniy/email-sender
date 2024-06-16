package com.vlkevheniy.emailsender;

import com.vlkevheniy.common.dto.MessageDto;
import com.vlkevheniy.emailsender.model.MessageData;
import com.vlkevheniy.emailsender.model.MessageStatus;
import com.vlkevheniy.emailsender.repository.MessageRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.MailException;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {EmailSenderApplication.class, TestElasticsearchConfiguration.class, KafkaTestConfig.class})
@TestPropertySource(properties = {
        "spring.mail.host=smtp.test.com",
        "spring.mail.port=587",
        "spring.mail.username=testuser",
        "spring.mail.password=testpassword",
        "spring.mail.properties.mail.smtp.auth=true",
        "spring.mail.properties.mail.smtp.starttls.enable=true",
        "management.health.mail.enabled=false"
})
public class EmailServiceIntegrationTest {

    @Autowired
    private KafkaTemplate<String, MessageDto> kafkaTemplate;

    @Autowired
    private MessageRepository messageRepository;

    @MockBean
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
    }

    @Test
    void testSendEmailSuccess() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        MessageDto messageDto = new MessageDto();
        messageDto.setTo("test@example.com");
        messageDto.setSubject("Test Subject");
        messageDto.setContent("Test Content");
        kafkaTemplate.send("messageReceived", messageDto);

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<MessageData> messages = StreamSupport
                    .stream(messageRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());
            assertThat(messages).hasSize(1);
            MessageData messageData = messages.get(0);
            assertThat(messageData.getStatus()).isEqualTo(MessageStatus.SENT);
            assertThat(messageData.getAttemptCount()).isEqualTo(0);
        });
    }

    @Test
    void testSendEmailFailure() {
        doThrow(new MailException("Failed to send email") {}).when(mailSender).send(any(SimpleMailMessage.class));

        MessageDto messageDto = new MessageDto();
        messageDto.setTo("test@example.com");
        messageDto.setSubject("Test Subject");
        messageDto.setContent("Test Content");
        kafkaTemplate.send("messageReceived", messageDto);

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<MessageData> messages = StreamSupport
                    .stream(messageRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());
            assertThat(messages).hasSize(1);
            MessageData messageData = messages.get(0);
            assertThat(messageData.getStatus()).isEqualTo(MessageStatus.FAILED);
            assertThat(messageData.getAttemptCount()).isEqualTo(1);
            assertThat(messageData.getErrorMessage()).contains("Failed to send email");
        });
    }
}
