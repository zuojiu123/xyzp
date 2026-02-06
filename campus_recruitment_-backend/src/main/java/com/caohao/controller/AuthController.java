package com.caohao.controller;

import com.caohao.common.Result;
import com.caohao.pojo.param.UserParam;
import com.caohao.security.util.JwtTokenUtil;
import com.caohao.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result login(@RequestBody UserParam loginParam) {
        try {
            // 认证用户
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginParam.getUserName(), loginParam.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);
            
            // 获取用户信息
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", userService.queryByUsername(loginParam.getUserName()));
            
            return Result.ok(result);
        } catch (Exception e) {
            return Result.failed("用户名或密码错误");
        }
    }
}