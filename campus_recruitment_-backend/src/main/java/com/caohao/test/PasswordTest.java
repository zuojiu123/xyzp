package com.caohao.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 测试密码
        String plainPassword = "123456";
        
        // 生成新的加密密码
        String encodedPassword = encoder.encode(plainPassword);
        System.out.println("明文密码: " + plainPassword);
        System.out.println("加密密码: " + encodedPassword);
        
        // 验证密码
        boolean matches = encoder.matches(plainPassword, encodedPassword);
        System.out.println("密码匹配: " + matches);
        
        // 测试数据库中的密码格式
        String dbPassword = "$2a$10$eImiTXuWVxfM37uY4JANjOEXAcTaUg4VM6Io8Rg.xRmNfVOwBd/Oa";
        boolean dbMatches = encoder.matches(plainPassword, dbPassword);
        System.out.println("数据库密码匹配: " + dbMatches);
        
        // 测试其他密码
        String otherPassword = "Hh123@qqcom";
        String otherEncoded = encoder.encode(otherPassword);
        System.out.println("\n其他密码: " + otherPassword);
        System.out.println("加密结果: " + otherEncoded);
        boolean otherMatches = encoder.matches(otherPassword, otherEncoded);
        System.out.println("匹配结果: " + otherMatches);
    }
}