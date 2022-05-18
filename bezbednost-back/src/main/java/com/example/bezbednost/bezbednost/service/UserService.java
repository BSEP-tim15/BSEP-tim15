package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.dto.PasswordDto;
import com.example.bezbednost.bezbednost.dto.UserDto;
import com.example.bezbednost.bezbednost.iservice.IRoleService;
import com.example.bezbednost.bezbednost.iservice.IUserService;
import com.example.bezbednost.bezbednost.mapper.UserMapper;
import com.example.bezbednost.bezbednost.model.PasswordResetToken;
import com.example.bezbednost.bezbednost.model.Role;
import com.example.bezbednost.bezbednost.model.User;
import com.example.bezbednost.bezbednost.repository.IPasswordResetTokenRepository;
import com.example.bezbednost.bezbednost.repository.IUserRepository;
import lombok.AllArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
@AllArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRoleService roleService;
    private final JavaMailSender mailSender;
    private final IPasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(UserDto userDto) {
        User user = UserMapper.mapDtoToUser(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        List<Role> roles = roleService.findByName(getUserRole(userDto.getRole()));
        user.setRoles(roles);
        String verificationCode = RandomString.make(64);
        user.setVerificationCode(verificationCode);
        sendVerificationEmail(user);
        userRepository.save(user);

        return user;
    }

    @Override
    public boolean isUserRegistered(String username) {
        User user = findByUsername(username);
        return user != null;
    }

    private String getUserRole(String role){
        if(role.contains("root")){
            return "organization";
        } else if(role.contains("intermediate")){
            return "service";
        } else if(role.contains("end-entity")) {
            return "user";
        } else {
            return "admin";
        }
    }

    @Override
    public String findUserRole(Integer id) {
        Optional<User> user = this.userRepository.findById(id);
        List<Role> roles = user.get().getRoles();
        return roles.get(0).getName();
    }

    @Override
    public void sendVerificationEmail(User user) {
        String subject = "Please verify your registration!";
        String sender = "Public key infrastructure";
        String content = "<p>Dear " + user.getName() + ", <p>";
        String verifyURL = "http://localhost:3000/verify/code=" + user.getVerificationCode();
        content += "<h3><a href=\"" + verifyURL + "\">VERIFY ACCOUNT</a></h3>";
        content += "<p>Thank you,<br>PKI</p>";

        System.out.println(user.getEmail());

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom("publickeyinfrastructuresomn@gmail.com", sender);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(content, true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mailSender.send(message);
    }

    @Override
    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);
        if (user == null) {
            return false;
        } else {
            userRepository.approve(user.getId());
            return true;
        }
    }

    @Override
    public void sendRecoveryURL(String email, String token){
        User user = userRepository.findByEmail(email);
        String subject = "Recover your account";
        String sender = "Public key infrastructure";
        String content = "<p>Dear " + user.getName() + ", <p>";
        String resetURL = "http://localhost:3000/resetPassword/token=" + token;
        content += "Reset password: <a href=\"" + resetURL + "\">link</a>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom("publickeyinfrastructuresomn@gmail.com", sender);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(content, true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mailSender.send(message);
    }

    @Override
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email);

        String token = UUID.randomUUID().toString();

        createPasswordResetTokenForUser(user, token);

        sendRecoveryURL(email, token);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);
        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

    @Override
    public void changePassword(PasswordDto passwordDto) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(passwordDto.getToken());
        passwordDto.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
        userRepository.changePassword(passwordResetToken.getUser().getEmail(), passwordDto.getPassword());
    }

    private void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
    }


}
