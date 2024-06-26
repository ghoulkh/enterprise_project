package com.enterprise.backend.service;

import com.enterprise.backend.model.entity.Authority;
import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.model.enums.OrderStatus;
import com.enterprise.backend.util.Constants;
import com.enterprise.backend.util.HTMLTemplateReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Set;

@Service
@Log4j2
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${app.admin.mail}")
    private String adminEmail;

    @Value("${app.admin.phone}")
    private String adminPhone;

    @Value("${app.front-end.domain}")
    private String domainFrontEnd;

    private String buildForgotPasswordMessage(String codeResetPassword) {
        StringBuilder builder = new StringBuilder();
        builder.append("Reset password\n")
                .append("Code will expire within 5 minutes\n")
                .append("Code: ");
        if (StringUtils.isNotEmpty(codeResetPassword)) {
            builder.append(codeResetPassword);
        }
        return builder.toString();
    }

    private void sendMail(String content, String subject, String to, boolean isHtml) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, Constants.UTF_8);
            helper.setText(content, isHtml);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(adminEmail);
            javaMailSender.send(mimeMessage);
            log.info("Mail sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Error while sending mail to {}: {}", to, e.getMessage(), e);
        }
    }

    public void sendNewOrder(String htmlContent, Set<User> receivers) {
        if (receivers == null || receivers.isEmpty()) {
            return;
        }
        receivers.forEach(receiver -> {
            if (receiver.getAuthorities().stream()
                    .anyMatch(authority -> Authority.Role.ROLE_SUPER_ADMIN.equals(authority.getRole())
                            || Authority.Role.ROLE_ADMIN.equals(authority.getRole()))) {
                String adminSubject = "Bạn có một đơn hàng mới";
                new Thread(() -> sendMail(htmlContent, adminSubject, receiver.getEmail(), true)).start();
            } else {
                String receiverSubject = "Bạn vừa đặt một đơn hàng mới tại " + domainFrontEnd;
                new Thread(() -> sendMail(htmlContent, receiverSubject, receiver.getEmail(), true)).start();
            }
        });
    }

    public void sendUpdateOrder(String htmlContent, OrderStatus status, String receiver) {
        StringBuilder receiverSubject = new StringBuilder();
        switch (status) {
            case PENDING:
                receiverSubject.append("Đơn hàng của bạn đã được vận chuyển");
                break;
            case COMPLETED:
                receiverSubject.append("Đơn hàng của bạn đã được hoàn thành");
                break;
            case CANCELLED:
                receiverSubject.append("Đơn hàng của bạn đã bị hủy");
                break;
            default:
                return;
        }
        new Thread(() -> sendMail(htmlContent, String.valueOf(receiverSubject), receiver, true)).start();
    }

    @Async
    public void sendMailForgotPassword(String codeResetPass, String email, String fullName) {
        String filePath = "./src/main/resources/email/mailTemplate.html";
        try {
            String htmlTemplate = HTMLTemplateReader.readTemplate(filePath);
            String htmlBody = htmlTemplate.replace("{{ fullName }}", fullName)
                    .replace("{{ code }}", codeResetPass);
            String subject = "Forgot password!!!";
            sendMail(htmlBody, subject, email, true);
        } catch (Exception e) {
            log.error("Failed to send mail reset password: {}", e.getMessage(), e);
        }
    }

    @Async
    public void sendMailNewOrder(String htmlContent, Set<User> receiver) {
        sendNewOrder(htmlContent, receiver);
    }
}