package com.xm.controller;

import com.xm.dto.ChangePasswordDTO;
import com.xm.result.Result;
import com.xm.service.EmployeeService;
import com.xm.entity.Employee;
import com.xm.vo.EmployeeVO;
import com.xm.page.page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "员工管理", description = "员工信息的增删改查相关接口")
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
    @Operation(summary = "添加员工", description = "新增员工信息，包括基本信息和账号信息")
    @PostMapping
    public Result addEmployee(@RequestBody Employee employee) {
        if (employeeService.addEmployee(employee).getCode() == 1) {
            return Result.success("员工添加成功");
        }
        return Result.error("员工添加失败");
    }

    /**
     * 更新员工状态
     * @param id 员工ID
     * @param enabled 状态值（1 启用，0 禁用）
     * @return Result
     */
    @Operation(summary = "更新员工状态", description = "启用或禁用员工账号")
    @PutMapping("/{id}")
    public Result updateEmployeeStatus(@PathVariable int id, @RequestParam int enabled) {
        return employeeService.updateEmployeeStatus(id, enabled);
    }

    /**
     * 更新员工信息
     * @param employee 员工信息（包含 ID）
     * @return Result<EmployeeVO>
     */
    @Operation(summary = "更新员工信息", description = "修改员工的基本信息和账号信息")
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
    @Operation(summary = "删除员工", description = "根据员工ID删除员工信息")
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
    @Operation(summary = "分页查询员工列表", description = "分页获取员工信息列表")
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
    @Operation(summary = "查询员工信息", description = "根据员工ID查询员工详细信息")
    @GetMapping("/{id}")
    public Result getEmployeeById(@PathVariable int id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return Result.success(employee);
        }
        return Result.error("未找到该员工");
    }

    /**
     * 重置员工密码
     * @param id 员工ID
     * @return Result
     */
    @Operation(summary = "重置员工密码", description = "将指定员工的密码重置为默认密码xm123456")
    @PutMapping("/reset-password/{id}")
    public Result resetPassword(@PathVariable int id) {
        return employeeService.resetPassword(id);
    }

    /**
     * 修改员工密码
     * @param changePasswordDTO 修改密码信息
     * @return Result
     */
    @Operation(summary = "修改员工密码", description = "修改员工密码")
    @PutMapping("/change-password")
    public Result changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        return employeeService.changePassword(changePasswordDTO);
    }

}