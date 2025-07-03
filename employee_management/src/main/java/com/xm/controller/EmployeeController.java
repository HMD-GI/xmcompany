package com.xm.controller;

import com.xm.result.Result;
import com.xm.service.EmployeeService;
import com.xm.entity.Employee;
import com.xm.vo.EmployeeVO;
import com.xm.page.page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/xm/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 添加员工
     * @param employee 员工信息
     * @return Result<EmployeeVO>
     */
    @PostMapping
    public Result addEmployee(@RequestBody Employee employee) {
        if (employeeService.addEmployee(employee).getCode() == 1) {
            return Result.success();
        }
        return Result.error("员工添加失败");
    }

    /**
     * 更新员工状态
     * @param id 员工ID
     * @param enabled 状态值（1 启用，0 禁用）
     * @return Result
     */
    @PutMapping("/{id}")
    public Result updateEmployeeStatus(@PathVariable int id, @RequestParam int enabled) {
        return employeeService.updateEmployeeStatus(id, enabled);
    }

    /**
     * 更新员工信息
     * @param employee 员工信息（包含 ID）
     * @return Result<EmployeeVO>
     */
    @PutMapping("/update")
    public Result updateEmployee(@RequestBody Employee employee) {
        if (employee.getId() <= 0) {
            return Result.error("无效的员工ID");
        }

        return employeeService. updateEmployee(employee);
    }

    /**
     * 删除员工
     * @param id 员工ID
     * @return Result
     */
    @DeleteMapping("/{id}")
    public Result deleteEmployee(@PathVariable int id) {
        return employeeService.deleteEmployee(id);
    }
    
    /**
     * 分页查询员工列表
     * @param currentPage 当前页码，默认为1
     * @param pageSize 每页显示数量，默认为10
     * @return Result<page<Employee>> 分页结果
     */
    @GetMapping("/list")
    public Result<page<EmployeeVO>> getEmployeeList(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize) {
        return employeeService.getEmployeeList(currentPage, pageSize);
    }

    /**
     * 根据员工ID查询员工信息
     * @param id 员工ID
     * @return Result<Employee>
     */
    @GetMapping("/{id}")
    public Result getEmployeeById(@PathVariable int id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return Result.success(employee);
        }
        return Result.error("未找到该员工");
    }
}