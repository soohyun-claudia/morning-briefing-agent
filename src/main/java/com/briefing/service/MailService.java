package com.briefing.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${mail.to}")
    private String to;

//    @Value("${mail.subject}")
//    private String subject;

    // JavaMailSender 주입
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 이메일 발송 메서드
    public void sendBriefing(String briefingText) throws MessagingException, UnsupportedEncodingException {
        String htmlContent = convertToHtml(briefingText);
        String subject = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")) + " Morning Briefing";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);  // 받는 사람
        helper.setSubject(new String(subject.getBytes("UTF-8"), "UTF-8"));  // 제목
        helper.setText(htmlContent, true);  // 본문

        mailSender.send(message);
    }

    private String convertToHtml(String markdown) {
        List<Extension> extensions = List.of(TablesExtension.create());
        Parser parser = Parser.builder()
                .extensions(extensions)
                .build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(extensions)
                .build();
        return renderer.render(document);
    }
}
