package com.xm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xm.entity.employeeLogin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface LoginMapper extends BaseMapper<employeeLogin> {
    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return employeeLogin 对象
     */
    @Select("SELECT * FROM employee_login WHERE username = #{username}")
    employeeLogin selectBy(@Param("username") String username);
}