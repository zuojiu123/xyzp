package com.caohao.security.filter;


import com.caohao.security.util.JwtTokenUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author caohao
 * @Description 请求拦截器，根据传过来的token，进行认证
 * @Date 2021-08-24
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Resource
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader(jwtTokenUtil.getHeader());
        System.out.println("JWT过滤器 - 请求URL: " + request.getRequestURI());
        System.out.println("JWT过滤器 - Header名称: " + jwtTokenUtil.getHeader());
        System.out.println("JWT过滤器 - 获取到的authHeader: " + authHeader);
        
        String token = null;
        if (authHeader != null) {
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7); // 去掉"Bearer "前缀
            } else {
                token = authHeader; // 直接使用token
            }
            System.out.println("JWT过滤器 - 解析后的token: " + token);
        }
        
        if (!StringUtils.isEmpty(token)) {
            String username = jwtTokenUtil.getUsernameFromToken(token);
            System.out.println("JWT过滤器 - 从 token 解析出的用户名: " + username);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("JWT过滤器 - 加载的UserDetails: " + userDetails);
                
                if (jwtTokenUtil.validateToken(token, userDetails)) {
                    System.out.println("JWT过滤器 - token验证成功，设置认证信息");
                    // 将用户信息存入 authentication，方便后续校验
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // 将 authentication 存入 ThreadLocal，方便后续获取用户信息
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    System.out.println("JWT过滤器 - token验证失败");
                }
            } else {
                System.out.println("JWT过滤器 - username为null或已有认证信息");
            }
        } else {
            System.out.println("JWT过滤器 - 没有找到token");
        }
        chain.doFilter(request, response);
    }
}
