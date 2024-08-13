package com.zayn.common.interceptors;

import cn.hutool.core.util.StrUtil;
import com.zayn.common.utils.UserContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zayn
 * * @date 2024/7/8/下午1:06
 */
public class UserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取用户信息
        String userInfo = request.getHeader("user-info");
        
        if (StrUtil.isNotBlank(userInfo)) {
            // 有用户信息，放行
            UserContext.setUser(Long.valueOf(userInfo));
        }
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.removeUser();
    }
}
