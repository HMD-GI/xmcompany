package com.xm.Interceptor;

import com.xm.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        String token = null;
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletReq = ((ServletServerHttpRequest) request).getServletRequest();
            token = servletReq.getParameter("token");      // 前端连接时拼在 URL 后
            if (!StringUtils.hasLength(token)) {
                token = servletReq.getHeader("Authorization");
            }
            if (StringUtils.hasLength(token) && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
        }

        if (!StringUtils.hasLength(token)) return false;

        try {
            Claims claims = JwtUtils.parseJWT(token);
            String username = claims.get("name", String.class);
            attributes.put("username", username);          // 放入 WebSocket Session 属性
            return true;
        } catch (Exception e) {
            return false;                                    // 握手直接失败
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) { }
}