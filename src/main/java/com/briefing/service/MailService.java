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

/**
 * Gmail SMTP를 통해 브리핑 이메일을 발송하는 서비스
 *
 * <p>마크다운 형식의 브리핑 텍스트를 HTML로 변환하여 이메일로 발송한다.
 * 이메일 제목은 "yyyy/MM Morning Briefing" 형식으로 동적 생성된다.
 *
 * <p>HTML 변환에는 commonmark 라이브러리를 사용하며,
 * 표(table) 렌더링을 위해 {@code TablesExtension} 을 적용한다.
 */
@Component
public class MailService {

    /** Gmail SMTP 이메일 발송에 사용할 JavaMailSender */
    private final JavaMailSender mailSender;

    /** application.yml에서 주입받는 수신자 이메일 주소 */
    @Value("${mail.to}")
    private String to;

    /**
     * JavaMailSender를 생성자 주입으로 받는다.
     *
     * <p>Spring Boot가 application.yml의 mail 설정을 읽어
     * {@link JavaMailSender} 객체를 자동으로 생성하여 주입해준다.
     *
     * @param mailSender Spring이 주입하는 JavaMailSender 객체
     */
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 브리핑 텍스트를 HTML로 변환하여 이메일로 발송한다.
     *
     * <p>처리 흐름:
     * <ol>
     *   <li>마크다운 브리핑 텍스트를 HTML로 변환</li>
     *   <li>이메일 제목 동적 생성 (yyyy/MM Morning Briefing)</li>
     *   <li>MimeMessage로 HTML 이메일 발송</li>
     * </ol>
     *
     * @param briefingText Claude API가 생성한 마크다운 형식의 브리핑 텍스트
     * @throws MessagingException 이메일 발송 실패 시
     * @throws UnsupportedEncodingException 이메일 제목 인코딩 실패 시
     */
    public void sendBriefing(String briefingText) throws MessagingException, UnsupportedEncodingException {
        String htmlContent = convertToHtml(briefingText);
        String subject = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")) + " Morning Briefing";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);  // 받는 사람
        helper.setSubject(subject);  // 제목
        helper.setText(htmlContent, true);  // 본문

        mailSender.send(message);
    }

    /**
     * 마크다운 텍스트를 HTML로 변환한다.
     *
     * <p>단일 책임 원칙(SRP)에 따라 변환 로직을 별도 메서드로 분리하였다.
     * {@code TablesExtension} 을 적용하여 마크다운 표(table)도 HTML로 변환한다.
     *
     * @param markdown 변환할 마크다운 텍스트
     * @return HTML로 변환된 문자열
     */
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
