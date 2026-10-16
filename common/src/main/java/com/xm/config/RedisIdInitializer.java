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
        redisIdGenerator.initId("leave", 1); // 添加请假ID初始化
        redisIdGenerator.initId("salary", 1); // 添加薪资配置ID初始化，起始值1000
        redisIdGenerator.initId("payroll", 1); // 添加工资单ID初始化，起始值10000
        redisIdGenerator.initId("supplier", 1003); // 初始化供应商ID
        redisIdGenerator.initId("purchase_request", 1); // 初始化采购申请ID
        redisIdGenerator.initId("purchase_order", 1); // 初始化采购订单ID
        redisIdGenerator.initId("stock", 10001);// 初始化库存ID，起始值为10001
        redisIdGenerator.initId("stock_operation", 10001);// 初始化库存操作记录ID，起始值为10001
        redisIdGenerator.initId("production_project", 0);// 初始化库存操作记录ID，起始值为1
        redisIdGenerator.initId("pproduction_progress", 0);// 初始化库存操作记录ID，起始值为1
        redisIdGenerator.initId("sale", 0);// 初始化销售记录ID，起始值为1
        // 其他业务ID初始化...
        
        System.out.println("Redis ID 初始化完成");
    }
} 