package com.xm.controller;

import com.xm.dto.StockOperationQueryDTO;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.StockOperationService;
import com.xm.vo.StockOperationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 库存操作记录控制器
 */
@Tag(name = "库存操作记录", description = "库存操作记录的查询相关接口")
@Slf4j
@RestController
@RequestMapping("/xm/stock-operation")
public class StockOperationController {

    @Autowired
    private StockOperationService stockOperationService; // 注入库存操作记录服务

    /**
     * 查询操作记录详情
     * @param id 操作记录ID
     * @return Result<StockOperationVO>
     */
    @Operation(summary = "查询操作记录详情", description = "根据ID查询库存操作记录详细信息")
    @GetMapping("/{id}")
    public Result<StockOperationVO> getStockOperationById(@PathVariable int id) {
        log.info("查询库存操作记录详情, ID: {}", id);
        return stockOperationService.getStockOperationById(id);
    }

    /**
     * 分页查询操作记录列表
     * @param currentPage 当前页码，默认为1
     * @param pageSize 每页显示数量，默认为10
     * @param queryDTO 查询条件
     * @return Result<page<StockOperationVO>>
     */
    @Operation(summary = "分页查询操作记录", description = "根据条件分页查询库存操作记录列表")
    @GetMapping("/list")
    public Result<page<StockOperationVO>> getStockOperationList(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            StockOperationQueryDTO queryDTO) {
        log.info("分页查询库存操作记录列表: 页码={}, 每页数量={}, 查询条件={}", currentPage, pageSize, queryDTO);
        return stockOperationService.getStockOperationList(currentPage, pageSize, queryDTO);
    }
} 