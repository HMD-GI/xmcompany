package com.xm.controller;

import com.xm.dto.PurchaseOrderQueryDTO;
import com.xm.dto.PurchaseOrderStatusDTO;
import com.xm.entity.PurchaseOrder;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.PurchaseOrderService;
import com.xm.vo.PurchaseOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 采购订单管理控制器
 */
@Tag(name = "采购订单管理", description = "采购订单的增删改查相关接口")
@Slf4j
@RestController
@RequestMapping("/xm/purchase-order")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService; // 注入采购订单服务

    /**
     * 新增采购订单
     * @param order 采购订单信息
     * @return Result
     */
    @Operation(summary = "新增采购订单", description = "新增采购订单信息")
    @PostMapping
    public Result addPurchaseOrder(@RequestBody PurchaseOrder order) {
        log.info("新增采购订单: {}", order);
        return purchaseOrderService.addPurchaseOrder(order);
    }

    /**
     * 更新采购订单
     * @param order 采购订单信息
     * @return Result
     */
    @Operation(summary = "更新采购订单", description = "修改采购订单信息")
    @PutMapping
    public Result updatePurchaseOrder(@RequestBody PurchaseOrder order) {
        log.info("更新采购订单: {}", order);
        return purchaseOrderService.updatePurchaseOrder(order);
    }

    /**
     * 作废采购订单
     * @param id 采购订单ID
     * @return Result
     */
    @Operation(summary = "作废采购订单", description = "将采购订单状态变更为已作废")
    @PutMapping("/cancel/{id}")
    public Result cancelPurchaseOrder(@PathVariable int id) {
        log.info("作废采购订单, ID: {}", id);
        return purchaseOrderService.cancelPurchaseOrder(id);
    }

    /**
     * 变更采购订单状态
     * @param statusDTO 状态变更DTO
     * @return Result
     */
    @Operation(summary = "变更采购订单状态", description = "修改采购订单状态")
    @PutMapping("/status")
    public Result updatePurchaseOrderStatus(@RequestBody PurchaseOrderStatusDTO statusDTO) {
        log.info("变更采购订单状态: {}", statusDTO);
        return purchaseOrderService.updatePurchaseOrderStatus(statusDTO);
    }

    /**
     * 查询采购订单详情
     * @param id 采购订单ID
     * @return Result<PurchaseOrderVO>
     */
    @Operation(summary = "查询采购订单详情", description = "根据ID查询采购订单详细信息")
    @GetMapping("/{id}")
    public Result<PurchaseOrderVO> getPurchaseOrderById(@PathVariable int id) {
        log.info("查询采购订单详情, ID: {}", id);
        return purchaseOrderService.getPurchaseOrderById(id);
    }

    /**
     * 分页查询采购订单列表
     * @param currentPage 当前页码，默认为1
     * @param pageSize 每页显示数量，默认为10
     * @param queryDTO 查询条件
     * @return Result<page<PurchaseOrderVO>>
     */
    @Operation(summary = "分页查询采购订单", description = "根据条件分页查询采购订单列表")
    @GetMapping("/list")
    public Result<page<PurchaseOrderVO>> getPurchaseOrderList(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            PurchaseOrderQueryDTO queryDTO) {
        log.info("分页查询采购订单列表: 页码={}, 每页数量={}, 查询条件={}", currentPage, pageSize, queryDTO);
        return purchaseOrderService.getPurchaseOrderList(currentPage, pageSize, queryDTO);
    }
} 