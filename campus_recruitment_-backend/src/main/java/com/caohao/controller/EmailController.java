package com.caohao.controller;

import com.caohao.common.Result;
import com.caohao.common.utils.IDGenerator;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
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
        message.setText("您的验证码为：" + authCode + "，有效期为5分钟。若非本人操作，请忽略此邮件。");

        try {
            mailSender.send(message);
            stringRedisTemplate.opsForValue().set("AuthCode" + receiver, authCode, AUTH_CODE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            stringRedisTemplate.opsForValue().set(sendLockKey, "1", SEND_LOCK_SECONDS, TimeUnit.SECONDS);
            return Result.ok("验证码发送成功");
        } catch (Exception e) {
            return Result.failed("验证码发送失败，请稍后重试");
        }
    }
}
