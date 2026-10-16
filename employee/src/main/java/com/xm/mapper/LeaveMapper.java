package com.xm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xm.entity.Leave;
import org.apache.ibatis.annotations.Mapper;

/**
 * 请假Mapper接口
 */
@Mapper
public interface LeaveMapper extends BaseMapper<Leave> {
    // 继承BaseMapper，获取基本的CRUD功能
} 