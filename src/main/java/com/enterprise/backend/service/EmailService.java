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

    private String messageForgotPasswordBuilder(String codeResetPassword) {
        StringBuilder builder = new StringBuilder();
        builder.append("Reset password\n" +
                "Code will expire within 5 minutes\n").append("Code: ");
        if (StringUtils.isNotEmpty(codeResetPassword)) {
            builder.append(codeResetPassword);
        }
        return builder.toString();
    }

    private void sendSimpleMail(String codeResetPass, String email) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, Constants.UTF_8);
            helper.setText(messageForgotPasswordBuilder(codeResetPass), false); // Use this or above line.
            helper.setTo(email);
            helper.setSubject("Forgot password!!!");
            helper.setFrom(adminEmail);
            javaMailSender.send(mimeMessage);
            log.info("Forgot Password Mail Sent Successfully...");
        }

        catch (Exception e) {
            log.error("Error while Sending Forgot Password Mail: {}", e.getMessage(), e);
        }
    }

    public void sendNewOrder(String htmlContent, String receiver) {
        try {

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, Constants.UTF_8);
            helper.setText(htmlContent, true); // Use this or above line.
            helper.setTo(adminEmail);
            helper.setSubject("Bạn có một đơn hàng mới");
            helper.setFrom(adminEmail);
            javaMailSender.send(mimeMessage);
            log.info("Mail Admin New Order Sent Successfully...");
        }

        catch (Exception e) {
            log.error("Error while Sending Mail Admin New Order: {}", e.getMessage(), e);
        }
        sendToReceiver(htmlContent, receiver);
    }


    private void sendToReceiver(String htmlContent, String receiverEmail) {

        try {

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, Constants.UTF_8);
            helper.setText(htmlContent, true); // Use this or above line.
            helper.setTo(receiverEmail);
            helper.setSubject("Bạn vừa đặt một đơn hàng mới tại " + domainFrontEnd);
            helper.setFrom(adminEmail);
            javaMailSender.send(mimeMessage);
            log.info("Mail New Order Sent Successfully...");
        }

        catch (Exception e) {
            log.error("Error while Sending Mail New Order: {} ", e.getMessage(), e);
        }
    }

    @Async
    public void sendMailForgotPassword(String codeResetPass, String email) {
        sendSimpleMail(codeResetPass, email);
    }

    @Async
    public void sendMailNewOrder(String htmlContent, String receiver) {
        sendNewOrder(htmlContent, receiver);
    }


}
