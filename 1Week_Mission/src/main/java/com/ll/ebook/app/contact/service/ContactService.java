package com.ll.ebook.app.contact.service;

import com.ll.ebook.app.member.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ContactService {
    private JavaMailSender emailSender;

    public void sendSimpleMessage(Member member, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("a2346532@gmail.com");
        message.setTo(member.getEmail());
        message.setSubject(title);
        message.setText(text);
        emailSender.send(message);
    }

    public void sendSimpleMessage(Member member, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("a2346532@gmail.com");
        message.setTo(member.getEmail());
        message.setSubject("임시 비밀번호");
        message.setText(text);
        emailSender.send(message);
    }
}