package se.brankoov.spring.security.consumer;

import se.brankoov.spring.security.config.RabbitConfig;
import se.brankoov.spring.security.consumer.dto.EmailRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Tar nu emot EmailRequestDTO automatiskt (tack vare Jackson Converter)
    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void handleMessage(EmailRequestDTO emailRequest) {
        log.info("📧 MOCK EMAIL SERVICE STARTED");
        log.info("Sending to: {}", emailRequest.to());
        log.info("Subject: {}", emailRequest.subject());
        log.info("Body: {}", emailRequest.body());
        log.info("📧 EMAIL SENT (Simulated)");
    }
}