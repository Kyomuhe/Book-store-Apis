package com.example.kay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WeeklySummaryService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    @Value("${app.admin.emails}")
    private List<String> adminEmails;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // Runs every Monday at 9 AM
    @Scheduled(cron = "0 0 9 * * MON")
    public void sendWeeklySummary() {
        try {
            String emailContent = generateWeeklySummaryContent();
            sendSummaryEmail(emailContent);
        } catch (Exception e) {
            System.err.println("Failed to send weekly summary: " + e.getMessage());
        }
    }

    private String generateWeeklySummaryContent() throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/weekly-summary.html");
        String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        // Getting date range for the past week
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(7);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        // statistics
        long totalUsers = userService.getTotalUserCount();
        long newUsersThisWeek = userService.getUsersCreatedInLastWeek();
        long loginAttempts = getLoginAttemptsThisWeek();
        long successfulLogins = getSuccessfulLoginsThisWeek();
        long welcomeEmailsSent = getWelcomeEmailsSentThisWeek();

        return template
                .replace("{{weekStart}}", weekStart.format(formatter))
                .replace("{{weekEnd}}", today.minusDays(1).format(formatter))
                .replace("{{totalUsers}}", String.valueOf(totalUsers))
                .replace("{{newUsersThisWeek}}", String.valueOf(newUsersThisWeek))
                .replace("{{loginAttempts}}", String.valueOf(loginAttempts))
                .replace("{{successfulLogins}}", String.valueOf(successfulLogins))
                .replace("{{welcomeEmailsSent}}", String.valueOf(welcomeEmailsSent));
    }

    private void sendSummaryEmail(String content) throws MessagingException {
        for (String adminEmail : adminEmails) {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(adminEmail);
            helper.setFrom(fromEmail);
            helper.setSubject("Weekly Summary Report - " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            helper.setText(content, true);

            mailSender.send(message);
        }
        System.out.println("Weekly summary sent to " + adminEmails.size() + " admins");
    }

    private long getLoginAttemptsThisWeek() {
        return 0;
    }

    private long getSuccessfulLoginsThisWeek() {
        return 0;
    }

    private long getWelcomeEmailsSentThisWeek() {
        return 0;
    }
}
