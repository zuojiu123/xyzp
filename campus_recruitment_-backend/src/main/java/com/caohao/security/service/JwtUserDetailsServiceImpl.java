package com.caohao.security.service;



import com.caohao.common.enums.impl.UserRoleEnum;
import com.caohao.pojo.model.UserModel;
import com.caohao.security.entity.JwtUser;
import com.caohao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userService.selectByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("%s.这个用户不存在", username));
        }
        
        // 获取用户角色
        String role = "Normal_User"; // 默认角色
        
        if (user.getRole() != null && !user.getRole().isEmpty()) {
            role = user.getRole();
        }
        
        return new JwtUser(user.getUserName(),user.getNickName(), user.getPassword(), user.getAvatar(),AuthorityUtils.commaSeparatedStringToAuthorityList(role));
    }
}
