package com.example.crudthymeilif.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationCode(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@crossfit.com");
            message.setTo(toEmail);
            message.setSubject("Codi de Verificació - CrossFit Competition");
            message.setText("El teu codi de verificació és: " + code + "\n\n" +
                    "Aquest codi expirarà en 15 minuts.\n\n" +
                    "Si no has sol·licitat aquest codi, ignora aquest correu.");
            
            mailSender.send(message);
        } catch (Exception e) {
            // En desarrollo, si no hay servidor SMTP, solo log el código
            System.out.println("==============================================");
            System.out.println("CÓDIGO DE VERIFICACIÓN (para desarrollo):");
            System.out.println("Email: " + toEmail);
            System.out.println("Código: " + code);
            System.out.println("==============================================");
        }
    }
}
