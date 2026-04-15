package com.caohao.controller;

import com.caohao.common.Result;
import com.caohao.common.utils.IDGenerator;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Api(tags = "Email")
@RestController
@RequestMapping("/email")
@CrossOrigin
public class EmailController {
    private static final Logger log = LoggerFactory.getLogger(EmailController.class);
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final long AUTH_CODE_EXPIRE_SECONDS = 300;
    private static final long SEND_LOCK_SECONDS = 60;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    @GetMapping("/getAuthCode")
    public Result getAuthCode(String receiver) {
        if (receiver == null || receiver.trim().isEmpty()) {
            return Result.failed("收件邮箱不能为空");
        }
        receiver = receiver.trim();
        if (!EMAIL_PATTERN.matcher(receiver).matches()) {
            return Result.failed("邮箱格式不正确");
        }

        String sendLockKey = "AuthCodeLock:" + receiver;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(sendLockKey))) {
            return Result.failed("验证码发送过于频繁，请稍后再试");
        }

        if (mailSender == null || senderEmail == null || senderEmail.trim().isEmpty()) {
            return Result.failed("邮件服务未配置，请联系管理员");
        }

        String authCode = IDGenerator.authCode();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(receiver);
        message.setSubject("校园招聘系统验证码");
        message.setText("您的验证码为：" + authCode + "，有效期 5 分钟。若非本人操作，请忽略此邮件。");
        log.info("验证码为 {}", authCode);

        try {
            mailSender.send(message);
            stringRedisTemplate.opsForValue()
                    .set("AuthCode" + receiver, authCode, AUTH_CODE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            stringRedisTemplate.opsForValue()
                    .set(sendLockKey, "1", SEND_LOCK_SECONDS, TimeUnit.SECONDS);
            return Result.ok("验证码发送成功");
        } catch (MailException e) {
            log.error("Failed to send auth code email to {}", receiver, e);
            return Result.failed(resolveMailErrorMessage(e));
        } catch (Exception e) {
            log.error("Unexpected error when sending auth code email to {}", receiver, e);
            return Result.failed("验证码发送失败，请稍后重试");
        }
    }

    private String resolveMailErrorMessage(Exception e) {
        String message = e.getMessage();
        if (message == null) {
            return "邮件服务暂时不可用，请稍后重试";
        }
        String lowerMessage = message.toLowerCase();
        if (lowerMessage.contains("connect") || lowerMessage.contains("timeout")) {
            return "邮件服务器连接失败，请检查网络或 SMTP 配置";
        }
        if (lowerMessage.contains("auth")) {
            return "邮箱账号或授权码配置错误";
        }
        return "验证码发送失败，请稍后重试";
    }
}
