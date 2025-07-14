package com.xm.controller;

import com.xm.entity.Customer;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.CustomerService;
import com.xm.vo.CustomerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "客户管理", description = "客户信息的增删改查相关接口")
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
    @Operation(summary = "添加客户", description = "新增客户信息，包括基本信息和联系方式等")
    @PostMapping
    public Result addCustomer(@RequestBody Customer customer) {
        return customerService.addCustomer(customer);
    }

    /**
     * 更新客户信息
     * @param customer 客户信息
     * @return Result
     */
    @Operation(summary = "更新客户信息", description = "修改客户的基本信息和联系方式")
    @PutMapping
    public Result updateCustomer(@RequestBody Customer customer) {
        return customerService.updateCustomer(customer);
    }

    /**
     * 删除客户
     * @param id 客户ID
     * @return Result
     */
    @Operation(summary = "删除客户", description = "根据客户ID删除客户信息")
    @DeleteMapping("/{id}")
    public Result deleteCustomer(@PathVariable int id) {
        return customerService.deleteCustomer(id);
    }
    
    /**
     * 根据ID查询客户
     * @param id 客户ID
     * @return Result<CustomerVO>
     */
    @Operation(summary = "查询客户信息", description = "根据客户ID查询客户详细信息")
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
    @Operation(summary = "分页查询客户列表", description = "分页获取客户信息列表")
    @GetMapping("/list")
    public Result<page<CustomerVO>> getCustomerList(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize) {
        return customerService.getCustomerList(currentPage, pageSize);
    }
} 