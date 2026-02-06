package com.caohao.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 为新用户生成加密密码
        String password = "123456";
        String encodedPassword = encoder.encode(password);
        
        System.out.println("原密码: " + password);
        System.out.println("加密后: " + encodedPassword);
        
        // 验证密码
        boolean matches = encoder.matches(password, encodedPassword);
        System.out.println("密码验证: " + matches);
        
        // 为文档中的测试账号生成密码
        System.out.println("\n=== 测试账号密码 ===");
        String[] testPasswords = {"123456", "Hh123@qqcom"};
        
        for (String pwd : testPasswords) {
            String encoded = encoder.encode(pwd);
            System.out.println("密码: " + pwd + " -> " + encoded);
        }
    }
}