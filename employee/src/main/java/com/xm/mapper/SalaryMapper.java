package com.xm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xm.entity.Salary;
import org.apache.ibatis.annotations.Mapper;

/**
 * 薪资配置Mapper接口
 */
@Mapper
public interface SalaryMapper extends BaseMapper<Salary> {
    // 继承BaseMapper，获取基本的CRUD功能
} 