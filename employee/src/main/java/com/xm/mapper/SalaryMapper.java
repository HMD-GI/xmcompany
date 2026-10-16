package com.xm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.dto.SalaryQueryDTO;
import com.xm.entity.Salary;
import com.xm.vo.SalaryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 薪资配置Mapper接口
 */
@Mapper
public interface SalaryMapper extends BaseMapper<Salary> {
    // 继承BaseMapper，获取基本的CRUD功能
    
    /**
     * 分页查询薪资配置列表
     * @param page 分页参数
     * @param queryDTO 查询条件
     * @return Page<SalaryVO>
     */
    Page<Salary> selectSalaryPage(Page<Salary> page, @Param("query") SalaryQueryDTO queryDTO);
}