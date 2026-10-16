package com.xm.controller;

import com.xm.dto.ProductionProgressQueryDTO;
import com.xm.dto.ProductionProgressUpdateDTO;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.ProductionProgressService;
import com.xm.vo.ProductionProgressVO;
import com.xm.vo.ProgressVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 生产进度控制器
 */

@Tag(name = "生产进度管理", description = "生产进度的查询与操作相关接口")
@Slf4j
@RestController
@RequestMapping("/xm/production/progress")
public class ProductionProgressController {

    @Autowired
    private ProductionProgressService productionProgressService; // 注入生产进度服务

    /**
     * 更新生产进度
     * @param progressUpdateDTO 进度更新信息
     * @return Result
     */
    @Operation(summary = "更新生产进度", description = "更新项目生产进度并自动入库")
    @PostMapping("/update")
    public Result updateProgress(@RequestBody ProductionProgressUpdateDTO progressUpdateDTO) {
        log.info("更新生产进度: {}", progressUpdateDTO);
        return productionProgressService.updateProgress(progressUpdateDTO);
    }

    /**
     * 查询进度记录详情
     * @param id 记录ID
     * @return Result<ProductionProgressVO>
     */
    @Operation(summary = "查询进度记录详情", description = "根据ID查询生产进度记录详细信息")
    @GetMapping("/{id}")
    public Result<ProductionProgressVO> getProgressById(@PathVariable int id) {
        log.info("查询进度记录详情, ID: {}", id);
        return productionProgressService.getProgressById(id);
    }

    /**
     * 分页查询项目进度记录
     * @param currentPage 当前页码，默认为1
     * @param pageSize 每页显示数量，默认为10
     * @param queryDTO 查询条件
     * @return Result<page<ProductionProgressVO>>
     */
    @Operation(summary = "分页查询进度记录", description = "根据条件分页查询生产进度记录列表")
    @GetMapping("/list")
    public Result<page<ProductionProgressVO>> getProgressList(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            ProductionProgressQueryDTO queryDTO) {
        log.info("分页查询项目进度记录: 页码={}, 每页数量={}, 查询条件={}", currentPage, pageSize, queryDTO);
        return productionProgressService.getProgressList(currentPage, pageSize, queryDTO);
    }
}