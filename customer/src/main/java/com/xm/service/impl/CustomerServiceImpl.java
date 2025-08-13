package com.xm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.entity.Customer;
import com.xm.mapper.CustomerMapper;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.CustomerService;
import com.xm.utils.RedisIdGenerator;
import com.xm.vo.CustomerVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private RedisIdGenerator redisIdGenerator;

    /**
     * 添加客户
     * @param customer 客户信息
     * @return Result
     */
    @Override
    public Result addCustomer(Customer customer) {
        // 设置创建时间和更新时间
        customer.setCreateTime(LocalDateTime.now());
        customer.setUpdateTime(LocalDateTime.now());
        
        // 使用Redis生成ID
        int id = redisIdGenerator.generateId("customer");
        customer.setId(id);
        
        // 如果客户状态为0，设置默认状态为1（潜在客户）
        if (customer.getStatus() == 0) {
            customer.setStatus(1); // 设置默认状态为潜在客户
        }

        if (customerMapper.insert(customer) > 0) {
            return Result.success("客户添加成功");
        }
        return Result.error("客户添加失败");
    }

    /**
     * 更新客户信息
     * @param customer 客户信息
     * @return Result
     */
    @Override
    @Transactional //存在“先读后写”并发风险（读→校验→更新），需保证原子性。
    public Result updateCustomer(Customer customer) {
        // 检查客户是否存在
        Customer existCustomer = customerMapper.selectById(customer.getId());
        if (existCustomer == null) {
            return Result.error("客户不存在");
        }
        
        // 设置更新时间
        customer.setUpdateTime(LocalDateTime.now());
        
        if (customerMapper.updateById(customer) > 0) {
            return Result.success("客户更新成功");
        }
        return Result.error("客户更新失败");
    }

    /**
     * 删除客户
     * @param id 客户ID
     * @return Result
     */
    @Override
    @Transactional //存在“先读后写”并发风险（读→校验→更新），需保证原子性。
    public Result deleteCustomer(int id) {
        // 检查客户是否存在
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            return Result.error("客户不存在");
        }
        
        if (customerMapper.deleteById(id) > 0) {
            return Result.success("客户删除成功");
        }
        return Result.error("客户删除失败");
    }

    /**
     * 根据ID查询客户
     * @param id 客户ID
     * @return Result<CustomerVO> 客户详情
     */
    @Override
    public Result<Customer> getCustomerById(int id) {
        // 查询客户信息
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            return Result.error("客户不存在");
        }
        
        return Result.success(customer);
    }

    /**
     * 分页查询客户列表
     * @param currentPage 当前页码
     * @param pageSize 每页显示数量
     * @return Result<page<CustomerVO>> 分页结果
     */
    // TODO 分页查询客户列表改为动态条件查询
    @Override
    public Result<page<CustomerVO>> getCustomerList(int currentPage, int pageSize) {
        // 创建分页对象
        Page<Customer> pageInfo = new Page<>(currentPage, pageSize);
        
        // 创建查询条件
        LambdaQueryWrapper<Customer> queryWrapper = new LambdaQueryWrapper<>();
        // 可以根据需求添加排序条件，例如按创建时间倒序排列
        queryWrapper.orderByDesc(Customer::getCreateTime);
        
        // 执行分页查询
        Page<Customer> customerPage = customerMapper.selectPage(pageInfo, queryWrapper);
        System.out.println(customerPage.getRecords());
        
        // 将Customer列表转换为CustomerVO列表
        List<CustomerVO> voList = customerPage.getRecords().stream().map(customer -> {
            CustomerVO vo = new CustomerVO();
            BeanUtils.copyProperties(customer, vo);
            return vo;
        }).collect(Collectors.toList());
        
        // 创建自定义分页对象
        page<CustomerVO> result = new page<>();
        result.setPageSize(pageSize);
        result.setTotal((int) customerPage.getTotal());
        result.setList(voList);
        
        return Result.success(result);
    }
} 