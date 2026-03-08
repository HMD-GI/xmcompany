package com.xm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xm.dto.PayrollAdjustmentDTO;
import com.xm.dto.PayrollGenerationDTO;
import com.xm.dto.PayrollQueryDTO;
import com.xm.dto.SalaryDTO;
import com.xm.dto.SalaryQueryDTO;
import com.xm.entity.Payroll;
import com.xm.entity.Salary;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.vo.PayrollVO;
import com.xm.vo.SalaryVO;

/**
 * 薪资服务接口
 */
public interface SalaryService extends IService<Salary> {
    
    /**
     * 设置员工薪资配置
     * @param salaryDTO 薪资配置信息
     * @return Result
     */
    Result setSalary(SalaryDTO salaryDTO);
    
    /**
     * 获取员工薪资配置
     * @param employeeId 员工ID
     * @return Result<SalaryVO>
     */
    Result<SalaryVO> getSalaryByEmployeeId(int employeeId);
    
    /**
     * 分页查询薪资配置列表
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件
     * @return Result<page<SalaryVO>>
     */
    Result<page<SalaryVO>> getSalaryConfigList(int currentPage, int pageSize, SalaryQueryDTO queryDTO);
    
    /**
     * 更新员工薪资配置状态
     * @param id 薪资配置ID
     * @param status 状态（1:有效, 0:无效）
     * @return Result
     */
    Result updateSalaryStatus(int id, int status);
    
    /**
     * 生成月度工资单
     * @param generationDTO 生成参数
     * @return Result
     */
    Result generateMonthlyPayroll(PayrollGenerationDTO generationDTO);
    
    /**
     * 调整工资单
     * @param adjustmentDTO 调整信息
     * @return Result
     */
    Result adjustPayroll(PayrollAdjustmentDTO adjustmentDTO);
    
    /**
     * 发放工资
     * @param payrollId 工资单ID
     * @return Result
     */
    Result payPayroll(int payrollId);
    
    /**
     * 发放指定月份所有待发放工资
     * @param month 月份（格式：yyyy-MM）
     * @return Result
     */
    Result payAllPayrollByMonth(String month);
    
    /**
     * 获取工资单详情
     * @param payrollId 工资单ID
     * @return Result<PayrollVO>
     */
    Result<Payroll> getPayrollById(int payrollId);
    
    /**
     * 分页查询员工工资单列表（动态查询）
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件
     * @return Result<page<PayrollVO>>
     */
    Result<page<PayrollVO>> getEmployeePayrollList(int currentPage, int pageSize, PayrollQueryDTO queryDTO);
    
    /**
     * 分页查询月度工资单列表（动态查询）
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件
     * @return Result<page<PayrollVO>>
     */
    Result<page<PayrollVO>> getMonthlyPayrollList(int currentPage, int pageSize, PayrollQueryDTO queryDTO);
}