package com.enterprise.backend.service;

import com.enterprise.backend.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@Log4j2
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${app.admin.mail}")
    private String adminEmail;

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

    public void sendNewOrder(String htmlContent, String receiver) {
        String adminSubject = "Bạn có một đơn hàng mới";
        new Thread(() -> sendMail(htmlContent, adminSubject, adminEmail, true));

        String receiverSubject = "Bạn vừa đặt một đơn hàng mới tại " + domainFrontEnd;
        new Thread(() -> sendMail(htmlContent, receiverSubject, receiver, true));
    }

    @Async
    public void sendMailForgotPassword(String codeResetPass, String email) {
        String subject = "Forgot password!!!";
        String content = buildForgotPasswordMessage(codeResetPass);
        sendMail(content, subject, email, false);
    }

    @Async
    public void sendMailNewOrder(String htmlContent, String receiver) {
        sendNewOrder(htmlContent, receiver);
    }
}