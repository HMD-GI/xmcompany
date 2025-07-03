package com.xm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xm.entity.employeeLogin;
import com.xm.result.Result;
import com.xm.vo.employeeLoginVO;

public interface LoginService extends IService<employeeLogin> {
    /**
     * 登录方法
     * @param username 用户名
     * @param password 密码
     * @return employeeLogin 对象，如果失败返回 null
     */
    Result login(String username, String password);
    
    /**
     * 根据员工ID查询登录信息
     * @param employeeId 员工ID
     * @return 员工登录信息对象
     */
    employeeLogin getByEmployeeId(int employeeId);
    
    /**
     * 根据员工ID删除登录信息
     * @param employeeId 员工ID
     * @return 是否删除成功
     */
    boolean removeByEmployeeId(int employeeId);
}