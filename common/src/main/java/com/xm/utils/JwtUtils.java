package com.xm.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtils {

    private static final String signKey = "xmcompany123456";//签名密钥，增加长度和复杂度
    //TODO 将jwt有效时间改为50天
    private static final Long expire = 4320000000L; //有效时间

    /**
     * 生成JWT令牌
     * @param claims JWT第二部分负载 payload 中存储的内容
     * @return
     */
    public static String generateJwt(Map<String, Object> claims){
        if (signKey == null || signKey.isEmpty()) {
            throw new IllegalArgumentException("JWT签名密钥不能为空");
        }
        
        byte[] signKeyBytes = signKey.getBytes();
        
        String jwt = Jwts.builder()
                .addClaims(claims)//自定义信息（有效载荷）
                .signWith(SignatureAlgorithm.HS256, signKeyBytes)//签名算法（头部），使用字节数组
                .setExpiration(new Date(System.currentTimeMillis() + expire))//过期时间
                .compact();
        return jwt;
    }

    /**
     * 解析JWT令牌
     * @param jwt JWT令牌
     * @return JWT第二部分负载 payload 中存储的内容
     */
    public static Claims parseJWT(String jwt){
        String token;
        if (!jwt.startsWith("Bearer ")) {
            token=jwt;
        }else{
            // 如果有Bearer就提取Bearer后面的token部分
            token = jwt.substring(7);
        }
        

        
        Claims claims = Jwts.parser()
                .setSigningKey(signKey.getBytes())//指定签名密钥
                .parseClaimsJws(token)//指定令牌Token
                .getBody();
        return claims;
    }
    
    /**
     * 从JWT令牌中获取用户名
     * @param jwt JWT令牌
     * @return 用户名
     */
    public static String getUsername(String jwt) {
        Claims claims = parseJWT(jwt);
        return claims.get("username", String.class);
    }
    
    /**
     * 从JWT令牌中获取员工ID
     * @param jwt JWT令牌
     * @return 员工ID
     */
    public static Integer getEmployeeId(String jwt) {
        Claims claims = parseJWT(jwt);
        return claims.get("employeeId", Integer.class);
    }
    
    /**
     * 从JWT令牌中获取用户角色
     * @param jwt JWT令牌
     * @return 用户角色
     */
    public static String getRole(String jwt) {
        Claims claims = parseJWT(jwt);
        return claims.get("role", String.class);
    }
}