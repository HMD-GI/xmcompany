package com.xm.controller;

import com.xm.dto.PayrollAdjustmentDTO;
import com.xm.dto.PayrollGenerationDTO;
import com.xm.dto.SalaryDTO;
import com.xm.entity.Payroll;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.SalaryService;
import com.xm.vo.PayrollVO;
import com.xm.vo.SalaryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 薪资管理控制器
 */
@Tag(name = "薪资管理", description = "员工薪资配置和工资单管理相关接口")
@RestController
@RequestMapping("/xm/salary")
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
     * 生成月度工资单
     */
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
     * 查询员工工资单列表
     */
    @Operation(summary = "查询员工工资单列表", description = "分页查询指定员工的工资单记录，可按年月筛选")
    @GetMapping("/payroll/list/employee/{employeeId}")
    public Result<page<PayrollVO>> getEmployeePayrollList(
            @PathVariable int employeeId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize) {
        return salaryService.getPayrollList(employeeId, year, month, currentPage, pageSize);
    }
    
    /**
     * 查询月度工资单列表
     */
    @Operation(summary = "查询月度工资单列表", description = "分页查询指定月份的所有员工工资单，可按状态筛选")
    @GetMapping("/payroll/list/month/{month}")
    public Result<page<PayrollVO>> getMonthlyPayrollList(
            @PathVariable String month,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "20") int pageSize) {
        return salaryService.getPayrollListByMonth(month, status, currentPage, pageSize);
    }
} 