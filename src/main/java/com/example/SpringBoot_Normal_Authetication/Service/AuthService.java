package com.example.SpringBoot_Normal_Authetication.Service;



import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.example.SpringBoot_Normal_Authetication.Entity.User;
import com.example.SpringBoot_Normal_Authetication.Repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class AuthService {
	private static final Logger logger = LogManager.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JavaMailSender mailSender;

    public String register(String email) throws Exception {
    	logger.info("inside register....");
        if (userRepo.findByEmail(email).isPresent()) {
        	logger.error("The mail already present");
            return "User already registered.";
        }

        String rawPassword = generateRandomPassword();
        String encryptedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        User user = new User();
        user.setEmail(email);
        user.setEncryptedPassword(encryptedPassword);
        user.setToken(null); // No token during registration

        userRepo.save(user);
        sendEmail(email, rawPassword);

        return "Registered successfully. Check email for your password.";
    }

    public String login(String email, String inputPassword) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) return null;

        User user = userOpt.get();
        try {
        if (BCrypt.checkpw(inputPassword, user.getEncryptedPassword())) {
        	logger.debug("Checking the UserPassword and db Password");
            String newToken = UUID.randomUUID().toString();
            user.setToken(newToken);
            userRepo.save(user);
            return newToken;
        }
        }catch(Exception e) {
        	logger.error("Login exception handled",email);
        }
        return null;
    }

    public boolean isAuthenticated(String email, String token) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        return userOpt.isPresent() && token.equals(userOpt.get().getToken());
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private void sendEmail(String to, String password) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("Welcome - Your Login Password");
        helper.setText("Your password is: " + password);
        mailSender.send(message);
    }
}
