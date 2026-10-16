package com.xm.schedule;

import com.xm.dto.PayrollGenerationDTO;
import com.xm.service.SalaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 薪资定时任务
 */
@Component
public class PayrollSchedule {

    private static final Logger logger = LoggerFactory.getLogger(PayrollSchedule.class);
    
    @Autowired
    private SalaryService salaryService;
    
    /**
     * 每月25日自动生成下个月工资单
     * 可以根据实际需要调整定时策略
     */
    @Scheduled(cron = "0 0 2 25 * ?") // 每月25日凌晨2点执行
    public void generateMonthlyPayroll() {
        try {
            // 获取当前月份
            LocalDate today = LocalDate.now();
            LocalDate nextMonth = today.plusMonths(1);
            String monthStr = nextMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            
            logger.info("开始自动生成{}月份工资单", monthStr);
            
            // 调用服务生成工资单
            PayrollGenerationDTO dto = new PayrollGenerationDTO();
            dto.setMonth(monthStr);
            salaryService.generateMonthlyPayroll(dto);
            
            logger.info("{}月份工资单生成完成", monthStr);
        } catch (Exception e) {
            logger.error("自动生成工资单失败", e);
        }
    }
}