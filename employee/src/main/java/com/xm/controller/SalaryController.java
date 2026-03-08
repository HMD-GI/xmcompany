package com.xm.controller;

import com.xm.dto.PayrollAdjustmentDTO;
import com.xm.dto.PayrollGenerationDTO;
import com.xm.dto.PayrollQueryDTO;
import com.xm.dto.SalaryDTO;
import com.xm.dto.SalaryQueryDTO;
import com.xm.entity.Payroll;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.SalaryService;
import com.xm.vo.PayrollVO;
import com.xm.vo.SalaryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 薪资管理控制器
 */
@Tag(name = "薪资管理", description = "员工薪资配置和工资单管理相关接口")
@RestController
@RequestMapping("/xm/salary")
@Slf4j
public class SalaryController {

    @Autowired
    private SalaryService salaryService;
    
    /**
     * 设置员工薪资配置
     */
    @Operation(summary = "设置员工薪资配置", description = "设置或更新员工的基本工资、绩效、补贴等薪资组成部分")
    @PostMapping("/config")
    public Result setSalary(@RequestBody SalaryDTO salaryDTO) {
        return salaryService.setSalary(salaryDTO);
    }
    
    /**
     * 获取员工薪资配置
     */
    @Operation(summary = "获取员工薪资配置", description = "根据员工ID查询其薪资结构配置信息")
    @GetMapping("/config/{employeeId}")
    public Result<SalaryVO> getSalaryConfig(@PathVariable int employeeId) {
        return salaryService.getSalaryByEmployeeId(employeeId);
    }
    
    /**
     * 分页查询薪资配置列表
     */
    @Operation(summary = "分页查询薪资配置列表", description = "分页查询薪资配置列表，支持多种条件筛选")
    @GetMapping("/config/list")
    public Result<page<SalaryVO>> getSalaryConfigList(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            SalaryQueryDTO queryDTO) {
        log.info("分页查询薪资配置列表动态数据queryDTO：" + queryDTO);
        return salaryService.getSalaryConfigList(currentPage, pageSize, queryDTO);
    }
    
    /**
     * 更新员工薪资配置状态
     */
    @Operation(summary = "更新员工薪资配置状态", description = "更新员工薪资配置的状态（有效/无效）")
    @PutMapping("/config/status/{id}")
    public Result updateSalaryStatus(@PathVariable int id, @RequestParam int status) {
        return salaryService.updateSalaryStatus(id, status);
    }
    
    /**
     * 生成月度工资单
     */
    //TODO 设置定时任务自动生成工资单
    @Operation(summary = "生成月度工资单", description = "根据月份生成员工工资单，计算实际工资金额")
    @PostMapping("/payroll/generate")
    public Result generatePayroll(@RequestBody PayrollGenerationDTO generationDTO) {
        return salaryService.generateMonthlyPayroll(generationDTO);
    }
    
    /**
     * 调整工资单
     */
    @Operation(summary = "调整工资单", description = "对已生成的工资单进行金额调整")
    @PutMapping("/payroll/adjust")
    public Result adjustPayroll(@RequestBody PayrollAdjustmentDTO adjustmentDTO) {
        return salaryService.adjustPayroll(adjustmentDTO);
    }
    
    /**
     * 发放单个工资单
     */
    @Operation(summary = "发放单个工资单", description = "将指定工资单状态标记为已发放")
    @PutMapping("/payroll/pay/{payrollId}")
    public Result payPayroll(@PathVariable int payrollId) {
        return salaryService.payPayroll(payrollId);
    }
    
    /**
     * 批量发放月度工资单
     */
    @Operation(summary = "批量发放月度工资单", description = "批量将指定月份的所有工资单标记为已发放")
    @PutMapping("/payroll/payall/{month}")
    public Result payAllByMonth(@PathVariable String month) {
        return salaryService.payAllPayrollByMonth(month);
    }
    
    /**
     * 获取工资单详情
     */
    @Operation(summary = "获取工资单详情", description = "根据工资单ID查询工资单详细信息")
    @GetMapping("/payroll/{payrollId}")
    public Result<Payroll> getPayrollDetail(@PathVariable int payrollId) {
        return salaryService.getPayrollById(payrollId);
    }
    
    /**
     * 分页查询员工工资单列表（动态查询）
     */
    @Operation(summary = "分页查询员工工资单列表", description = "分页查询员工工资单列表，支持多种条件筛选")
    @GetMapping("/payroll/list/employee")
    public Result<page<PayrollVO>> getEmployeePayrollList(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            PayrollQueryDTO queryDTO) {
        log.info("分页查询员工工资单列表动态数据queryDTO：" + queryDTO);
        return salaryService.getEmployeePayrollList(currentPage, pageSize, queryDTO);
    }
    
    /**
     * 分页查询月度工资单列表（动态查询）
     */
    @Operation(summary = "分页查询月度工资单列表", description = "分页查询月度工资单列表，支持多种条件筛选")
    @GetMapping("/payroll/list/month")
    public Result<page<PayrollVO>> getMonthlyPayrollList(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "20") int pageSize,
            PayrollQueryDTO queryDTO) {
        log.info("分页查询月度工资单列表动态数据queryDTO：" + queryDTO);
        return salaryService.getMonthlyPayrollList(currentPage, pageSize, queryDTO);
    }
}