package se.brankoov.spring.security.consumer;

import jakarta.mail.internet.MimeMessage; // Ny import
import se.brankoov.spring.security.config.RabbitConfig;
import se.brankoov.spring.security.consumer.dto.EmailRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper; // Ny import
import org.springframework.stereotype.Service;

@Service
public class EmailConsumer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    public EmailConsumer(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void handleMessage(EmailRequestDTO emailRequest) {
        log.info("📧 Received email request for: {}", emailRequest.to());

        try {
            // Vi använder MimeMessage för att kunna sätta ett snyggt namn
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            // HÄR SÄTTER DU NAMNET: (Din adress, "Det namn som ska visas")
            helper.setFrom(fromEmail, "Todo App Support");

            helper.setTo(emailRequest.to());
            helper.setSubject(emailRequest.subject());
            helper.setText(emailRequest.body(), false); // false = vanlig text, true = HTML

            javaMailSender.send(message);

            log.info("✅ Email sent successfully to {}", emailRequest.to());
        } catch (Exception e) {
            log.error("❌ Failed to send email: {}", e.getMessage());
        }
    }
}