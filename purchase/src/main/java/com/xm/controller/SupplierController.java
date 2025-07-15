package com.xm.controller;

import com.xm.dto.SupplierQueryDTO;
import com.xm.dto.SupplierStatusDTO;
import com.xm.entity.Supplier;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.SupplierService;
import com.xm.vo.SupplierVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 供应商管理控制器
 */
@Tag(name = "供应商管理", description = "供应商信息的增删改查相关接口")
@Slf4j
@RestController
@RequestMapping("/xm/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService; // 注入供应商服务
    
    /**
     * 添加供应商
     * @param supplier 供应商信息
     * @return Result
     */
    @Operation(summary = "添加供应商", description = "新增供应商信息")
    @PostMapping
    public Result addSupplier(@RequestBody Supplier supplier) {
        log.info("添加供应商: {}", supplier);
        return supplierService.addSupplier(supplier);
    }
    
    /**
     * 更新供应商信息
     * @param supplier 供应商信息
     * @return Result
     */
    @Operation(summary = "更新供应商", description = "修改供应商基本信息")
    @PutMapping
    public Result updateSupplier(@RequestBody Supplier supplier) {
        log.info("更新供应商: {}", supplier);
        return supplierService.updateSupplier(supplier);
    }
    
    /**
     * 删除供应商
     * @param id 供应商ID
     * @return Result
     */
    @Operation(summary = "删除供应商", description = "根据ID删除供应商")
    @DeleteMapping("/{id}")
    public Result deleteSupplier(@PathVariable int id) {
        log.info("删除供应商, ID: {}", id);
        return supplierService.deleteSupplier(id);
    }
    
    /**
     * 根据ID查询供应商详情
     * @param id 供应商ID
     * @return Result<SupplierVO>
     */
    @Operation(summary = "查询供应商详情", description = "根据ID查询供应商详细信息")
    @GetMapping("/{id}")
    public Result<SupplierVO> getSupplierById(@PathVariable int id) {
        log.info("查询供应商详情, ID: {}", id);
        return supplierService.getSupplierById(id);
    }
    
    /**
     * 分页查询供应商列表
     * @param currentPage 当前页码，默认为1
     * @param pageSize 每页显示数量，默认为10
     * @param queryDTO 查询条件
     * @return Result<page<SupplierVO>>
     */
    @Operation(summary = "分页查询供应商", description = "根据条件分页查询供应商列表")
    @GetMapping("/list")
    public Result<page<SupplierVO>> getSupplierList(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            SupplierQueryDTO queryDTO) {
        log.info("分页查询供应商列表: 页码={}, 每页数量={}, 查询条件={}", currentPage, pageSize, queryDTO);
        return supplierService.getSupplierList(currentPage, pageSize, queryDTO);
    }
    
    /**
     * 更新供应商状态
     * @param statusDTO 状态更新DTO
     * @return Result
     */
    @Operation(summary = "更新供应商状态", description = "修改供应商的合作状态")
    @PutMapping("/status")
    public Result updateSupplierStatus(@RequestBody SupplierStatusDTO statusDTO) {
        log.info("更新供应商状态: {}", statusDTO);
        return supplierService.updateSupplierStatus(statusDTO);
    }
} 