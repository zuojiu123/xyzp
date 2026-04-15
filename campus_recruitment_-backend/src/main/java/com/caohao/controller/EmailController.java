package com.caohao.controller;

import com.caohao.common.Result;
import com.caohao.common.utils.IDGenerator;
import com.caohao.service.EmailAsyncService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    @Autowired
    private EmailAsyncService emailAsyncService;

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

        if (senderEmail == null || senderEmail.trim().isEmpty()) {
            return Result.failed("邮件服务未配置，请联系管理员");
        }

        String authCode = IDGenerator.authCode();
        log.info("验证码为 {}", authCode);
        String authCodeKey = "AuthCode" + receiver;

        try {
            stringRedisTemplate.opsForValue()
                    .set(authCodeKey, authCode, AUTH_CODE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            stringRedisTemplate.opsForValue()
                    .set(sendLockKey, "1", SEND_LOCK_SECONDS, TimeUnit.SECONDS);
            emailAsyncService.sendAuthCodeEmailAsync(receiver, senderEmail, authCode, authCodeKey, sendLockKey);
            return Result.ok("验证码已发送，请注意查收");
        } catch (Exception e) {
            log.error("Unexpected error when creating auth code task for {}", receiver, e);
            return Result.failed("验证码发送失败，请稍后重试");
        }
    }
}
