package com.xm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.dto.PayrollQueryDTO;
import com.xm.entity.Payroll;
import com.xm.vo.PayrollVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 薪资单Mapper接口
 */
@Mapper
public interface PayrollMapper extends BaseMapper<Payroll> {
    // 继承BaseMapper，获取基本的CRUD功能
    
    /**
     * 分页查询员工工资单列表
     * @param page 分页参数
     * @param queryDTO 查询条件
     * @return Page<PayrollVO>
     */
    Page<PayrollVO> selectEmployeePayrollPage(Page<Payroll> page, @Param("query") PayrollQueryDTO queryDTO);
    
    /**
     * 分页查询月度工资单列表
     * @param page 分页参数
     * @param queryDTO 查询条件
     * @return Page<PayrollVO>
     */
    Page<PayrollVO> selectMonthlyPayrollPage(Page<Payroll> page, @Param("query") PayrollQueryDTO queryDTO);
}