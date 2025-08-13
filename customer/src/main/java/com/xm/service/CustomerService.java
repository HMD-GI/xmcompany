package com.xm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xm.entity.Customer;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.vo.CustomerVO;

public interface CustomerService extends IService<Customer> {
    /**
     * 添加客户
     * @param customer 客户信息
     * @return Result
     */
    Result addCustomer(Customer customer);

    /**
     * 更新客户信息
     * @param customer 客户信息
     * @return Result
     */
    Result updateCustomer(Customer customer);

    /**
     * 删除客户
     * @param id 客户ID
     * @return Result
     */
    Result deleteCustomer(int id);
    
    /**
     * 根据ID查询客户
     * @param id 客户ID
     * @return Result<CustomerVO> 客户详情
     */
    Result<Customer> getCustomerById(int id);
    
    /**
     * 分页查询客户列表
     * @param currentPage 当前页码
     * @param pageSize 每页显示数量
     * @return Result<page<CustomerVO>> 分页结果
     */
    Result<page<CustomerVO>> getCustomerList(int currentPage, int pageSize);
} 