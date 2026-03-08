package com.xm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.dto.EmployeeQueryDTO;
import com.xm.entity.Employee;
import com.xm.vo.EmployeeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    /**
     * 分页查询员工列表
     * @param page 分页参数
     * @param queryDTO 查询条件
     * @return Page<Employee>
     */
    Page<EmployeeVO> selectEmployeePage(Page<Employee> page, @Param("query") EmployeeQueryDTO queryDTO);
}