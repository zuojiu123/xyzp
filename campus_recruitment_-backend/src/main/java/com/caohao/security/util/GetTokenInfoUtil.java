package com.caohao.security.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class GetTokenInfoUtil {
    /**
     * 获取登陆的用户名
     *
     * @author caohao
     * @since 2021-11-14 12:14:54
     */
    public static String getUsername() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            System.out.println("SecurityContext principal: " + principal);
            System.out.println("Principal class: " + (principal != null ? principal.getClass().getName() : "null"));
            
            String string = principal.toString();
            if (string.equals("anonymousUser")){
                System.out.println("返回noLogin，原因: anonymousUser");
                return "noLogin";
            } else {
                if (principal instanceof UserDetails) {
                    UserDetails userDetails = (UserDetails) principal;
                    String username = userDetails.getUsername();
                    System.out.println("获取到的用户名: " + username);
                    return username;
                } else {
                    System.out.println("Principal不是UserDetails类型，直接返回toString: " + string);
                    return string;
                }
            }
        } catch (Exception e) {
            System.out.println("获取用户名异常: " + e.getMessage());
            e.printStackTrace();
            return "noLogin";
        }
    }
}
