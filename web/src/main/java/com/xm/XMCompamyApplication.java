package com.xm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@EnableScheduling //开启定时任务

public class XMCompamyApplication {
    public static void main(String[] args) {
        SpringApplication.run(XMCompamyApplication.class, args);
    }
}