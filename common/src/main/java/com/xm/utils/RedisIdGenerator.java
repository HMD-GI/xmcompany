package com.xm.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 基于Redis的ID生成器
 * 使用Redis的incr命令实现分布式自增ID
 */
@Component
public class RedisIdGenerator {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private static final String ID_PREFIX = "xm:id:";
    
    /**
     * 为指定的业务生成自增ID
     * @param businessName 业务名称，例如"customer", "employee"等
     * @return 生成的自增ID
     */
    public int generateId(String businessName) {
        String key = ID_PREFIX + businessName;
        // 使用Redis的incr命令生成自增ID
        Long id = redisTemplate.opsForValue().increment(key);
        System.out.println("为" + businessName + "生成ID: " + id); // 调试日志
        return id != null ? id.intValue() : 0;
    }
    
    /**
     * 初始化指定业务的ID值
     * @param businessName 业务名称
     * @param initValue 初始值
     */
    public void initId(String businessName, int initValue) {
        String key = ID_PREFIX + businessName;
        // 只有当key不存在时才设置初始值
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(initValue));
        if (Boolean.TRUE.equals(result)) {
            System.out.println("初始化" + businessName + "的ID为: " + initValue);
        }
    }
} 