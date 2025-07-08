package com.xm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xm.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

} 