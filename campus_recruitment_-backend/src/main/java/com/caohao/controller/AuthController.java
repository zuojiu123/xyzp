package com.caohao.controller;

import com.caohao.common.Result;
import com.caohao.pojo.model.UserModel;
import com.caohao.pojo.param.UserParam;
import com.caohao.security.util.JwtTokenUtil;
import com.caohao.service.LoginLogService;
import com.caohao.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(tags = "Auth")
@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Resource
    private AuthenticationManager authenticationManager;
    
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    
    @Resource
    private UserService userService;

    @Resource
    private LoginLogService loginLogService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final int MAX_LOGIN_RETRY = 5;
    private static final long LOGIN_LOCK_MINUTES = 10;

    private String loginFailKey(String username) {
        return "LoginFail:" + username;
    }

    private String loginLockKey(String username) {
        return "LoginLock:" + username;
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.trim().isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.trim().isEmpty()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result login(@RequestBody UserParam loginParam, HttpServletRequest request) {
        try {
            String username = loginParam.getUserName();
            if (username == null || username.trim().isEmpty()) {
                return Result.failed("请输入用户名");
            }
            username = username.trim();
            String clientIp = getClientIp(request);
            String userAgent = request.getHeader("User-Agent");

            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(loginLockKey(username)))) {
                loginLogService.saveLoginLog(null, username, null, "LOCKED", "登录失败次数过多，请10分钟后再试", clientIp, userAgent);
                return Result.failed("登录失败次数过多，请10分钟后再试");
            }

            // 认证用户
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, loginParam.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);
            stringRedisTemplate.delete(loginFailKey(username));
            stringRedisTemplate.delete(loginLockKey(username));
            
            // 获取用户信息
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            UserModel user = userService.queryByUsername(username);
            result.put("user", user);
            loginLogService.saveLoginLog(
                    user != null ? user.getId() : null,
                    username,
                    user != null ? user.getRole() : null,
                    "SUCCESS",
                    "登录成功",
                    clientIp,
                    userAgent
            );
            
            return Result.ok(result);
        } catch (Exception e) {
            String username = loginParam.getUserName();
            if (username != null && !username.trim().isEmpty()) {
                String clientIp = getClientIp(request);
                String userAgent = request.getHeader("User-Agent");
                String failKey = loginFailKey(username.trim());
                Long failCount = stringRedisTemplate.opsForValue().increment(failKey);
                if (failCount != null && failCount == 1L) {
                    stringRedisTemplate.expire(failKey, LOGIN_LOCK_MINUTES, TimeUnit.MINUTES);
                }
                if (failCount != null && failCount >= MAX_LOGIN_RETRY) {
                    stringRedisTemplate.opsForValue().set(loginLockKey(username.trim()), "1", LOGIN_LOCK_MINUTES, TimeUnit.MINUTES);
                    stringRedisTemplate.delete(failKey);
                    loginLogService.saveLoginLog(null, username.trim(), null, "LOCKED", "登录失败次数过多，请10分钟后再试", clientIp, userAgent);
                    return Result.failed("登录失败次数过多，请10分钟后再试");
                }
                loginLogService.saveLoginLog(null, username.trim(), null, "FAILED", "用户名或密码错误", clientIp, userAgent);
            }
            return Result.failed("用户名或密码错误");
        }
    }
}
