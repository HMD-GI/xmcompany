package com.xm.controller;

import com.xm.dto.*;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.SaleOrderService;
import com.xm.vo.SaleOrderVO;
import com.xm.vo.SimpleSaleOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 销售订单控制器
 */


@Tag(name = "销售管理", description = "销售订单的查询与操作相关接口")
@Slf4j
@RestController
@RequestMapping("/xm/sale")
public class SaleOrderController {

    @Autowired
    private SaleOrderService saleOrderService; // 注入销售订单服务

    /**
     * 新增销售订单
     * @param addDTO 新增信息
     * @return Result
     */
    @Operation(summary = "新增销售订单", description = "创建新的销售订单")
    @PostMapping("/order")
    public Result addSaleOrder(@RequestBody SaleOrderAddDTO addDTO) {
        log.info("新增销售订单: {}", addDTO);
        return saleOrderService.addSaleOrder(addDTO);
    }

    /**
     * 更新销售订单
     * @param updateDTO 更新信息
     * @return Result
     */
    @Operation(summary = "更新销售订单", description = "修改销售订单信息")
    @PutMapping("/order")
    public Result updateSaleOrder(@RequestBody SaleOrderUpdateDTO updateDTO) {
        log.info("更新销售订单: {}", updateDTO);
        return saleOrderService.updateSaleOrder(updateDTO);
    }

    /**
     * 销售订单发货
     * @param shipDTO 发货信息
     * @return Result
     */
    @Operation(summary = "销售订单发货", description = "处理销售订单发货，调用库存出库接口")
    @PostMapping("/order/ship")
    public Result shipSaleOrder(@RequestBody SaleOrderShipDTO shipDTO) {
        log.info("销售订单发货: {}", shipDTO);
        return saleOrderService.shipSaleOrder(shipDTO);
    }

    /**
     * 更新订单状态
     * @param statusUpdateDTO 状态更新信息
     * @return Result
     */
    @Operation(summary = "更新订单状态", description = "修改销售订单的状态")
    @PutMapping("/order/status")
    public Result updateSaleOrderStatus(@RequestBody SaleOrderStatusUpdateDTO statusUpdateDTO) {
        log.info("更新订单状态: {}", statusUpdateDTO);
        return saleOrderService.updateSaleOrderStatus(statusUpdateDTO);
    }

    /**
     * 查询销售订单详情
     * @param id 订单ID
     * @return Result<SaleOrderVO>
     */
    @Operation(summary = "查询销售订单详情", description = "根据ID查询销售订单详细信息")
    @GetMapping("/order/{id}")
    public Result<SaleOrderVO> getSaleOrderById(@PathVariable int id) {
        log.info("查询销售订单详情, ID: {}", id);
        return saleOrderService.getSaleOrderById(id);
    }

    /**
     * 分页查询销售订单列表
     * @param currentPage 当前页码，默认为 1
     * @param pageSize 每页显示数量，默认为 10
     * @param queryDTO 查询条件
     * @return Result<page<SaleOrderVO>>
     */
    @Operation(summary = "分页查询销售订单", description = "根据条件分页查询销售订单列表")
    @GetMapping("/order/list")
    public Result<page<SimpleSaleOrderVO>> getSaleOrderList(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            SaleOrderQueryDTO queryDTO) {
        log.info("分页查询销售订单列表：页码={}, 每页数量={}, 查询条件={}", currentPage, pageSize, queryDTO);
        return saleOrderService.getSaleOrderList(currentPage, pageSize, queryDTO);
    }

    /**
     * 删除销售订单
     * @param id 订单 ID
     * @return Result
     */
    @Operation(summary = "删除销售订单", description = "根据 ID 删除销售订单")
    @DeleteMapping("/order/{id}")
    public Result deleteSaleOrder(@PathVariable int id) {
        log.info("删除销售订单，ID: {}", id);
        return saleOrderService.deleteSaleOrder(id);
    }
}