package com.xm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.dto.CustomerQueryDTO;
import com.xm.entity.Customer;
import com.xm.vo.CustomerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
    /**
     * 分页查询客户列表
     * @param page 分页参数
     * @param queryDTO 查询条件
     * @return Page<Customer>
     */
    Page<CustomerVO> selectCustomerPage(Page<Customer> page, @Param("query") CustomerQueryDTO queryDTO);
} 