package com.xm.controller;

import com.xm.entity.Customer;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.CustomerService;
import com.xm.vo.CustomerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/xm/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 添加客户
     * @param customer 客户信息
     * @return Result
     */
    @PostMapping
    public Result addCustomer(@RequestBody Customer customer) {
        return customerService.addCustomer(customer);
    }

    /**
     * 更新客户信息
     * @param customer 客户信息
     * @return Result
     */
    @PutMapping
    public Result updateCustomer(@RequestBody Customer customer) {
        return customerService.updateCustomer(customer);
    }

    /**
     * 删除客户
     * @param id 客户ID
     * @return Result
     */
    @DeleteMapping("/{id}")
    public Result deleteCustomer(@PathVariable int id) {
        return customerService.deleteCustomer(id);
    }
    
    /**
     * 根据ID查询客户
     * @param id 客户ID
     * @return Result<CustomerVO>
     */
    @GetMapping("/{id}")
    public Result<CustomerVO> getCustomerById(@PathVariable int id) {
        return customerService.getCustomerById(id);
    }
    
    /**
     * 分页查询客户列表
     * @param currentPage 当前页码，默认为1
     * @param pageSize 每页显示数量，默认为10
     * @return Result<page<CustomerVO>>
     */
    @GetMapping("/list")
    public Result<page<CustomerVO>> getCustomerList(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize) {
        return customerService.getCustomerList(currentPage, pageSize);
    }
} 