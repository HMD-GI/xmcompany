package com.xm.controller;

import com.xm.dto.PayrollAdjustmentDTO;
import com.xm.dto.PayrollGenerationDTO;
import com.xm.dto.SalaryDTO;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.SalaryService;
import com.xm.vo.PayrollVO;
import com.xm.vo.SalaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 薪资管理控制器
 */
@RestController
@RequestMapping("/xm/salary")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;
    
    /**
     * 设置员工薪资配置
     */
    @PostMapping("/config")
    public Result setSalary(@RequestBody SalaryDTO salaryDTO) {
        return salaryService.setSalary(salaryDTO);
    }
    
    /**
     * 获取员工薪资配置
     */
    @GetMapping("/config/{employeeId}")
    public Result<SalaryVO> getSalaryConfig(@PathVariable int employeeId) {
        return salaryService.getSalaryByEmployeeId(employeeId);
    }
    
    /**
     * 生成月度工资单
     */
    @PostMapping("/payroll/generate")
    public Result generatePayroll(@RequestBody PayrollGenerationDTO generationDTO) {
        return salaryService.generateMonthlyPayroll(generationDTO);
    }
    
    /**
     * 调整工资单
     */
    @PutMapping("/payroll/adjust")
    public Result adjustPayroll(@RequestBody PayrollAdjustmentDTO adjustmentDTO) {
        return salaryService.adjustPayroll(adjustmentDTO);
    }
    
    /**
     * 发放单个工资单
     */
    @PutMapping("/payroll/pay/{payrollId}")
    public Result payPayroll(@PathVariable int payrollId) {
        return salaryService.payPayroll(payrollId);
    }
    
    /**
     * 批量发放月度工资单
     */
    @PutMapping("/payroll/payall/{month}")
    public Result payAllByMonth(@PathVariable String month) {
        return salaryService.payAllPayrollByMonth(month);
    }
    
    /**
     * 获取工资单详情
     */
    @GetMapping("/payroll/{payrollId}")
    public Result<PayrollVO> getPayrollDetail(@PathVariable int payrollId) {
        return salaryService.getPayrollById(payrollId);
    }
    
    /**
     * 查询员工工资单列表
     */
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
    @GetMapping("/payroll/list/month/{month}")
    public Result<page<PayrollVO>> getMonthlyPayrollList(
            @PathVariable String month,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "20") int pageSize) {
        return salaryService.getPayrollListByMonth(month, status, currentPage, pageSize);
    }
} 