package com.caohao.controller;

import com.caohao.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@Api(tags = "Health")
@RestController
@RequestMapping("/health")
@CrossOrigin
public class HealthController {

    @ApiOperation("系统健康检查")
    @GetMapping("/check")
    public Result healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("message", "校园招聘系统后端运行正常");
        health.put("timestamp", System.currentTimeMillis());
        return Result.ok(health);
    }

    @ApiOperation("获取系统信息")
    @GetMapping("/info")
    public Result systemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "campus-recruitment-backend");
        info.put("version", "1.0.0");
        info.put("java.version", System.getProperty("java.version"));
        return Result.ok(info);
    }
}