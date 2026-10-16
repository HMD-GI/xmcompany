package com.xm.controller;

import com.xm.dto.StockInDTO;
import com.xm.dto.StockOutDTO;
import com.xm.dto.StockQueryDTO;
import com.xm.entity.Stock;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.StockService;
import com.xm.vo.SimpleStockVO;
import com.xm.vo.StockVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 库存管理控制器
 */
@Tag(name = "库存管理", description = "库存的查询与操作相关接口")
@Slf4j
@RestController
@RequestMapping("/xm/stock")
public class StockController {

    @Autowired
    private StockService stockService; // 注入库存服务

    /**
     * 入库操作
     * @param stockInDTO 入库信息
     * @return Result
     */
    @Operation(summary = "入库操作", description = "物料入库")
    @PostMapping("/in")
    public Result stockIn(@RequestBody StockInDTO stockInDTO) {
        log.info("入库操作: {}", stockInDTO);
        return stockService.stockIn(stockInDTO);
    }

    /**
     * 出库操作
     * @param stockOutDTO 出库信息
     * @return Result
     */
    @Operation(summary = "出库操作", description = "物料出库")
    @PostMapping("/out")
    public Result stockOut(@RequestBody StockOutDTO stockOutDTO) {
        log.info("出库操作: {}", stockOutDTO);
        return stockService.stockOut(stockOutDTO);
    }

    /**
     * 更新库存信息
     * @param stock 库存信息
     * @return Result
     */
    @Operation(summary = "更新库存", description = "修改库存信息，如安全库存等")
    @PutMapping
    public Result updateStock(@RequestBody Stock stock) {
        log.info("更新库存信息: {}", stock);
        return stockService.updateStock(stock);
    }

    /**
     * 查询库存详情
     * @param id 库存ID
     * @return Result<StockVO>
     */
    @Operation(summary = "查询库存详情", description = "根据ID查询库存详细信息")
    @GetMapping("/{id}")
    public Result<StockVO> getStockById(@PathVariable int id) {
        log.info("查询库存详情, ID: {}", id);
        return stockService.getStockById(id);
    }


    /**
     * 分页查询库存列表
     * @param currentPage 当前页码，默认为1
     * @param pageSize 每页显示数量，默认为10
     * @param queryDTO 查询条件
     * @return Result<page<StockVO>>
     */
    @Operation(summary = "分页查询库存", description = "根据条件分页查询库存列表")
    @GetMapping("/list")
    public Result<page<SimpleStockVO>> getStockList(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            StockQueryDTO queryDTO) {
        log.info("分页查询库存列表: 页码={}, 每页数量={}, 查询条件={}", currentPage, pageSize, queryDTO);
        return stockService.getStockList(currentPage, pageSize, queryDTO);
    }
} 