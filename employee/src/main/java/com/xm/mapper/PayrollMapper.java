package com.xm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xm.entity.Payroll;
import org.apache.ibatis.annotations.Mapper;

/**
 * 薪资单Mapper接口
 */
@Mapper
public interface PayrollMapper extends BaseMapper<Payroll> {
    // 继承BaseMapper，获取基本的CRUD功能
} 