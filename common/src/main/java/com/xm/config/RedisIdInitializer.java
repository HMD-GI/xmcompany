package com.xm.config;

import com.xm.utils.RedisIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Redis ID 初始化器
 * 系统启动时初始化各业务ID
 */
@Component
public class RedisIdInitializer implements CommandLineRunner {

    @Autowired
    private RedisIdGenerator redisIdGenerator;

    @Override
    public void run(String... args) {
        // 初始化各业务的ID起始值
        // 可以根据数据库当前最大ID来设置，这里简单设置为1000
        redisIdGenerator.initId("customer", 1);
        redisIdGenerator.initId("employee", 1);
        redisIdGenerator.initId("employeelogin", 1);
        // 其他业务ID初始化...
        
        System.out.println("Redis ID 初始化完成");
    }
} 