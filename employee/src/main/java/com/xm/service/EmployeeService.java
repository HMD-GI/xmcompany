package com.xm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xm.entity.Employee;
import com.xm.result.Result;
import com.xm.page.page;
import com.xm.vo.EmployeeVO;

public interface EmployeeService extends IService<Employee> {
    /**
     * 添加员工
     * @param employee 员工信息
     * @return Result
     */
    Result addEmployee(Employee employee);

    /**
     * 更新员工状态（启用/禁用）
     * @param id 员工ID
     * @param enabled 状态值（1 启用，0 禁用）
     * @return Result
     */
    Result updateEmployeeStatus(int id, int enabled);

    /**
     * 更新员工信息
     * @param employee 员工信息
     * @return Result
     */
    Result updateEmployee(Employee employee);

    /**
     * 删除员工
     * @param id 员工ID
     * @return Result
     */
    Result deleteEmployee(int id);
    
    /**
     * 分页查询员工列表
     * @param currentPage 当前页码
     * @param pageSize 每页显示数量
     * @return Result<page<Employee>> 分页结果
     */
    Result<page<EmployeeVO>> getEmployeeList(int currentPage, int pageSize);

}