package com.xm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xm.entity.Employee;
import com.xm.entity.employeeLogin;
import com.xm.mapper.EmployeeMapper;
import com.xm.result.Result;
import com.xm.service.EmployeeService;
import com.xm.page.page;
import com.xm.service.LoginService;
import com.xm.vo.EmployeeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private LoginService loginService; // 注入 LoginService
    @Override
    @Transactional // 启用事务
    public Result addEmployee(Employee employee) {
        // 设置默认值
        employee.setCreateTime(LocalDateTime.now());
        employee.setRole("USER"); // 默认角色为 USER
        employee.setEnabled(1); // 默认启用

        if (employeeMapper.insert(employee) > 0) {
            // 构建员工登录信息
            employeeLogin login = new employeeLogin();
            login.setUsername(employee.getUsername()); // 使用员工的用户名
            login.setPassword(employee.getPassword()); // 使用员工的密码
            login.setRole(employee.getRole()); // 角色同步
            login.setEnabled(employee.getEnabled()); // 启用状态同步
            login.setCreateTime(LocalDateTime.now()); // 创建时间
            login.setLastLoginTime(null); // 初始登录时间为 null
            login.setEmployeeId(employee.getId()); // 设置员工ID（此时已自动生成）

            // 插入登录信息
            if (loginService.save(login)) {
                return Result.success("员工及登录信息添加成功");
            } else {
                // 如果登录信息保存失败，删除已插入的员工信息
                employeeMapper.deleteById(employee.getId());
                return Result.error("员工登录信息添加失败");
            }
        }
        return Result.error("员工添加失败");
    }

    @Override
    @Transactional // 启用事务
    public Result updateEmployeeStatus(int id, int enabled) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            return Result.error("员工不存在");
        }
        employee.setEnabled(enabled);
        if (employeeMapper.updateById(employee) > 0) {
            // 更新登录表状态
            employeeLogin login = loginService.getById(employee.getId());
            if (login != null) {
                login.setEnabled(enabled);
                if (loginService.updateById(login)) {
                    return Result.success("状态更新成功");
                } else {
                    return Result.error("登录信息状态更新失败");
                }
            } else {
                return Result.error("登录信息不存在");
            }
        }
        return Result.error("状态更新失败");
    }

    @Override
    @Transactional // 启用事务
    public Result updateEmployee(Employee employee) {
        // 更新员工基本信息
        if (employeeMapper.updateById(employee) > 0) {
            // 获取当前登录信息
            employeeLogin login = loginService.getByEmployeeId(employee.getId());

            if (login != null) {
                // 同步更新登录信息中的字段
                login.setUsername(employee.getUsername());
                login.setPassword(employee.getPassword());
                login.setRole(employee.getRole());
                login.setEnabled(employee.getEnabled());

                if (loginService.updateById(login)) {
                    return Result.success("员工及登录信息更新成功");
                } else {
                    return Result.error("登录信息更新失败");
                }
            } else {
                return Result.error("登录信息不存在");
            }
        }
        return Result.error("员工信息更新失败");
    }

    @Override
    @Transactional // 启用事务
    public Result deleteEmployee(int id) {
        // 查询员工是否存在
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            return Result.error("员工不存在");
        }

        // 删除员工主表数据
        if (employeeMapper.deleteById(id) > 0) {
            // 删除登录信息
            if (loginService.removeByEmployeeId(id)) {
                return Result.success("员工及登录信息删除成功");
            } else {
                return Result.error("登录信息删除失败");
            }
        }
        return Result.error("员工删除失败");
    }
    
    @Override
    public Result<page<EmployeeVO>> getEmployeeList(int currentPage, int pageSize) {
        // 创建MyBatis-Plus的分页对象，用于查询Employee实体
        Page<Employee> pageInfo = new Page<>(currentPage, pageSize);
        
        // 创建查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        
        // 执行分页查询
        Page<Employee> employeePage = employeeMapper.selectPage(pageInfo, queryWrapper);
        
        // 将Employee列表转换为EmployeeVO列表
        List<EmployeeVO> voList = employeePage.getRecords().stream().map(employee -> {
            EmployeeVO vo = new EmployeeVO();
            BeanUtils.copyProperties(employee, vo);
            return vo;
        }).collect(Collectors.toList());
        
        // 创建自定义分页对象
        page<EmployeeVO> result = new page<>();
        result.setPageSize(pageSize);
        result.setTotal((int) employeePage.getTotal());
        result.setList(voList);
        
        return Result.success(result);
    }
}