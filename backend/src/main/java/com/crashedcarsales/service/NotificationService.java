package com.crashedcarsales.service;

import com.crashedcarsales.dto.CarResponse;
import com.crashedcarsales.dto.DealerProfile;
import com.crashedcarsales.entity.Car;
import com.crashedcarsales.entity.Dealer;
import com.crashedcarsales.entity.User;
import com.crashedcarsales.repository.CarRepository;
import com.crashedcarsales.repository.DealerRepository;
import com.crashedcarsales.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;
    private final DealerRepository dealerRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    @Value("${app.mail.from:noreply@crashedcarsales.com}")
    private String fromEmail;

    @Value("${app.mail.from-name:Crashed Car Sales}")
    private String fromName;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Autowired
    public NotificationService(JavaMailSender mailSender,
                               DealerRepository dealerRepository,
                               UserRepository userRepository,
                               CarRepository carRepository) {
        this.mailSender = mailSender;
        this.dealerRepository = dealerRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    /**
     * Send email notification to dealer when buyer shows interest in their car
     */
    public void notifyDealerOfBuyerInterest(UUID carId, UUID buyerId, String message) {
        logger.info("Sending buyer interest notification for car {} from buyer {}", carId, buyerId);

        try {
            // Get car details
            Car car = getCarById(carId);
            if (car == null) {
                logger.error("Car not found with ID: {}", carId);
                return;
            }

            // Get dealer details
            Dealer dealer = car.getDealer();
            if (dealer == null || dealer.getUser() == null) {
                logger.error("Dealer not found for car ID: {}", carId);
                return;
            }

            // Get buyer details (optional, for logging)
            User buyer = userRepository.findById(buyerId).orElse(null);

            // Send email notification
            sendBuyerInterestEmail(dealer, car, buyer, message);

            logger.info("Buyer interest notification sent successfully for car {} to dealer {}",
                carId, dealer.getUser().getEmail());

        } catch (Exception e) {
            logger.error("Failed to send buyer interest notification for car {}: {}", carId, e.getMessage(), e);
        }
    }

    /**
     * Send email notification to dealer when their car is favorited
     */
    public void notifyDealerOfCarFavorited(UUID carId, UUID buyerId) {
        logger.info("Sending favorite notification for car {} from buyer {}", carId, buyerId);

        try {
            // Get car details
            Car car = getCarById(carId);
            if (car == null) {
                logger.error("Car not found with ID: {}", carId);
                return;
            }

            // Get dealer details
            Dealer dealer = car.getDealer();
            if (dealer == null || dealer.getUser() == null) {
                logger.error("Dealer not found for car ID: {}", carId);
                return;
            }

            // Get buyer details
            User buyer = userRepository.findById(buyerId).orElse(null);

            // Send email notification
            sendCarFavoritedEmail(dealer, car, buyer);

            logger.info("Favorite notification sent successfully for car {} to dealer {}",
                carId, dealer.getUser().getEmail());

        } catch (Exception e) {
            logger.error("Failed to send favorite notification for car {}: {}", carId, e.getMessage(), e);
        }
    }

    /**
     * Send welcome email to new dealer
     */
    public void sendDealerWelcomeEmail(DealerProfile dealerProfile) {
        logger.info("Sending welcome email to new dealer: {}", dealerProfile.getEmail());

        try {
            String subject = "Welcome to Crashed Car Sales - Account Created Successfully";
            String htmlBody = buildDealerWelcomeEmail(dealerProfile);

            sendHtmlEmail(dealerProfile.getEmail(), subject, htmlBody);

            logger.info("Welcome email sent successfully to dealer: {}", dealerProfile.getEmail());

        } catch (Exception e) {
            logger.error("Failed to send welcome email to dealer {}: {}", dealerProfile.getEmail(), e.getMessage(), e);
        }
    }

    /**
     * Send email notification when car status changes
     */
    public void notifyDealerOfCarStatusChange(UUID carId, String oldStatus, String newStatus) {
        logger.info("Sending car status change notification for car {}: {} -> {}", carId, oldStatus, newStatus);

        try {
            // Get car details
            Car car = getCarById(carId);
            if (car == null) {
                logger.error("Car not found with ID: {}", carId);
                return;
            }

            // Get dealer details
            Dealer dealer = car.getDealer();
            if (dealer == null || dealer.getUser() == null) {
                logger.error("Dealer not found for car ID: {}", carId);
                return;
            }

            // Send email notification
            sendCarStatusChangeEmail(dealer, car, oldStatus, newStatus);

            logger.info("Status change notification sent successfully for car {} to dealer {}",
                carId, dealer.getUser().getEmail());

        } catch (Exception e) {
            logger.error("Failed to send status change notification for car {}: {}", carId, e.getMessage(), e);
        }
    }

    // ==================== EMAIL SENDING METHODS ====================

    /**
     * Send buyer interest email to dealer
     */
    private void sendBuyerInterestEmail(Dealer dealer, Car car, User buyer, String message) throws MessagingException {
        String subject = "New Buyer Interest in Your Car: " + car.getMake() + " " + car.getModel();

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("dealerName", dealer.getName());
        templateData.put("carMake", car.getMake());
        templateData.put("carModel", car.getModel());
        templateData.put("carYear", car.getYear());
        templateData.put("carPrice", car.getPrice());
        templateData.put("buyerEmail", buyer != null ? buyer.getEmail() : "Anonymous");
        templateData.put("message", message);
        templateData.put("currentDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        String htmlBody = buildEmailTemplate("buyer-interest-template", templateData);

        sendHtmlEmail(dealer.getUser().getEmail(), subject, htmlBody);
    }

    /**
     * Send car favorited email to dealer
     */
    private void sendCarFavoritedEmail(Dealer dealer, Car car, User buyer) throws MessagingException {
        String subject = "Your Car Was Added to Favorites: " + car.getMake() + " " + car.getModel();

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("dealerName", dealer.getName());
        templateData.put("carMake", car.getMake());
        templateData.put("carModel", car.getModel());
        templateData.put("carYear", car.getYear());
        templateData.put("carPrice", car.getPrice());
        templateData.put("buyerEmail", buyer != null ? buyer.getEmail() : "Anonymous");
        templateData.put("currentDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        String htmlBody = buildEmailTemplate("car-favorited-template", templateData);

        sendHtmlEmail(dealer.getUser().getEmail(), subject, htmlBody);
    }

    /**
     * Send car status change email to dealer
     */
    private void sendCarStatusChangeEmail(Dealer dealer, Car car, String oldStatus, String newStatus) throws MessagingException {
        String subject = "Car Status Updated: " + car.getMake() + " " + car.getModel();

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("dealerName", dealer.getName());
        templateData.put("carMake", car.getMake());
        templateData.put("carModel", car.getModel());
        templateData.put("carYear", car.getYear());
        templateData.put("oldStatus", oldStatus);
        templateData.put("newStatus", newStatus);
        templateData.put("currentDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        String htmlBody = buildEmailTemplate("car-status-change-template", templateData);

        sendHtmlEmail(dealer.getUser().getEmail(), subject, htmlBody);
    }

    /**
     * Send HTML email
     */
    private void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        try {
            helper.setFrom(fromEmail, fromName);
        } catch (UnsupportedEncodingException e) {
            helper.setFrom(fromEmail); // Fallback without name
        }
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(message);
        logger.debug("HTML email sent to: {}", to);
    }

    /**
     * Send plain text email (fallback)
     */
    private void sendPlainTextEmail(String to, String subject, String textBody) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(textBody);

            mailSender.send(message);
            logger.debug("Plain text email sent to: {}", to);
        } catch (MailException e) {
            logger.error("Failed to send plain text email to {}: {}", to, e.getMessage(), e);
        }
    }

    // ==================== EMAIL TEMPLATE METHODS ====================

    /**
     * Build email template with data
     */
    private String buildEmailTemplate(String templateName, Map<String, Object> data) {
        switch (templateName) {
            case "buyer-interest-template":
                return buildBuyerInterestTemplate(data);
            case "car-favorited-template":
                return buildCarFavoritedTemplate(data);
            case "car-status-change-template":
                return buildCarStatusChangeTemplate(data);
            case "dealer-welcome-template":
                return buildDealerWelcomeTemplate(data);
            default:
                return buildDefaultTemplate(data);
        }
    }

    /**
     * Build buyer interest email template
     */
    private String buildBuyerInterestTemplate(Map<String, Object> data) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>New Buyer Interest</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #007bff; color: white; padding: 20px; text-align: center; }
                    .content { background-color: #f8f9fa; padding: 20px; }
                    .car-details { background-color: white; padding: 15px; margin: 10px 0; border-radius: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>New Buyer Interest!</h1>
                    </div>
                    <div class="content">
                        <p>Dear %s,</p>
                        <p>Great news! Someone has shown interest in your car listing.</p>

                        <div class="car-details">
                            <h3>Car Details:</h3>
                            <p><strong>%s %s %d</strong></p>
                            <p><strong>Price:</strong> $%.2f</p>
                            <p><strong>Buyer Email:</strong> %s</p>
                            <p><strong>Message:</strong> %s</p>
                        </div>

                        <p>Please log in to your dashboard to respond to this inquiry.</p>
                        <p>Sent on: %s</p>
                    </div>
                    <div class="footer">
                        <p>Crashed Car Sales - Connecting buyers and sellers</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            data.get("dealerName"),
            data.get("carMake"), data.get("carModel"), data.get("carYear"),
            data.get("carPrice"),
            data.get("buyerEmail"),
            data.get("message"),
            data.get("currentDate")
        );
    }

    /**
     * Build car favorited email template
     */
    private String buildCarFavoritedTemplate(Map<String, Object> data) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Car Added to Favorites</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #28a745; color: white; padding: 20px; text-align: center; }
                    .content { background-color: #f8f9fa; padding: 20px; }
                    .car-details { background-color: white; padding: 15px; margin: 10px 0; border-radius: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Your Car Was Favorited!</h1>
                    </div>
                    <div class="content">
                        <p>Dear %s,</p>
                        <p>Someone has added your car to their favorites list!</p>

                        <div class="car-details">
                            <h3>Car Details:</h3>
                            <p><strong>%s %s %d</strong></p>
                            <p><strong>Price:</strong> $%.2f</p>
                            <p><strong>Favorited by:</strong> %s</p>
                        </div>

                        <p>This is a great sign of interest! The buyer may contact you soon.</p>
                        <p>Sent on: %s</p>
                    </div>
                    <div class="footer">
                        <p>Crashed Car Sales - Connecting buyers and sellers</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            data.get("dealerName"),
            data.get("carMake"), data.get("carModel"), data.get("carYear"),
            data.get("carPrice"),
            data.get("buyerEmail"),
            data.get("currentDate")
        );
    }

    /**
     * Build car status change email template
     */
    private String buildCarStatusChangeTemplate(Map<String, Object> data) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Car Status Updated</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #ffc107; color: #212529; padding: 20px; text-align: center; }
                    .content { background-color: #f8f9fa; padding: 20px; }
                    .car-details { background-color: white; padding: 15px; margin: 10px 0; border-radius: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Car Status Updated</h1>
                    </div>
                    <div class="content">
                        <p>Dear %s,</p>
                        <p>The status of one of your car listings has been updated.</p>

                        <div class="car-details">
                            <h3>Car Details:</h3>
                            <p><strong>%s %s %d</strong></p>
                            <p><strong>Previous Status:</strong> %s</p>
                            <p><strong>New Status:</strong> %s</p>
                        </div>

                        <p>Please log in to your dashboard to view the updated listing.</p>
                        <p>Sent on: %s</p>
                    </div>
                    <div class="footer">
                        <p>Crashed Car Sales - Connecting buyers and sellers</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            data.get("dealerName"),
            data.get("carMake"), data.get("carModel"), data.get("carYear"),
            data.get("oldStatus"),
            data.get("newStatus"),
            data.get("currentDate")
        );
    }

    /**
     * Build dealer welcome email template
     */
    private String buildDealerWelcomeEmail(DealerProfile dealerProfile) {
        Map<String, Object> data = new HashMap<>();
        data.put("dealerName", dealerProfile.getName());
        data.put("currentDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        return buildDealerWelcomeTemplate(data);
    }

    /**
     * Build dealer welcome email template
     */
    private String buildDealerWelcomeTemplate(Map<String, Object> data) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Welcome to Crashed Car Sales</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #007bff; color: white; padding: 20px; text-align: center; }
                    .content { background-color: #f8f9fa; padding: 20px; }
                    .welcome-box { background-color: white; padding: 15px; margin: 10px 0; border-radius: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to Crashed Car Sales!</h1>
                    </div>
                    <div class="content">
                        <p>Dear %s,</p>
                        <p>Welcome to our platform! Your dealer account has been created successfully.</p>

                        <div class="welcome-box">
                            <h3>Getting Started:</h3>
                            <ul>
                                <li>Log in to your dashboard</li>
                                <li>Add your car listings</li>
                                <li>Set up your dealer profile</li>
                                <li>Start connecting with buyers</li>
                            </ul>
                        </div>

                        <p>If you have any questions, please don't hesitate to contact our support team.</p>
                        <p>Welcome aboard!</p>
                        <p>Sent on: %s</p>
                    </div>
                    <div class="footer">
                        <p>Crashed Car Sales - Connecting buyers and sellers</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            data.get("dealerName"),
            data.get("currentDate")
        );
    }

    /**
     * Build default email template
     */
    private String buildDefaultTemplate(Map<String, Object> data) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Crashed Car Sales Notification</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #007bff; color: white; padding: 20px; text-align: center; }
                    .content { background-color: #f8f9fa; padding: 20px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Crashed Car Sales</h1>
                    </div>
                    <div class="content">
                        <p>You have received a notification from Crashed Car Sales.</p>
                        <p>Please log in to your dashboard for more details.</p>
                    </div>
                    <div class="footer">
                        <p>Crashed Car Sales - Connecting buyers and sellers</p>
                    </div>
                </div>
            </body>
            </html>
            """);
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Get car by ID (for internal use)
     */
    private Car getCarById(UUID carId) {
        logger.debug("Getting car by ID: {}", carId);
        return carRepository.findById(carId).orElse(null);
    }

    /**
     * Check if email notifications are enabled
     */
    public boolean areEmailNotificationsEnabled() {
        return mailUsername != null && !mailUsername.isEmpty();
    }

    /**
     * Send test email
     */
    public void sendTestEmail(String toEmail) {
        try {
            String subject = "Test Email from Crashed Car Sales";
            String htmlBody = """
                <!DOCTYPE html>
                <html>
                <head><title>Test Email</title></head>
                <body>
                    <h2>Test Email</h2>
                    <p>This is a test email from Crashed Car Sales notification system.</p>
                    <p>Sent at: """ + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + """
                </p>
                </body>
                </html>
                """;

            sendHtmlEmail(toEmail, subject, htmlBody);
            logger.info("Test email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send test email to {}: {}", toEmail, e.getMessage(), e);
        }
    }
}