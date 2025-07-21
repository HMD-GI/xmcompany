package com.xm.controller;

import com.xm.dto.ProductionProjectAddDTO;
import com.xm.dto.ProductionProjectQueryDTO;
import com.xm.dto.ProductionStatusUpdateDTO;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.ProductionProjectService;
import com.xm.vo.ProductionProjectVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 生产项目控制器
 */
@Tag(name = "生产项目管理", description = "生产项目的查询与操作相关接口")
@Slf4j
@RestController
@RequestMapping("/xm/production/project")
public class ProductionProjectController {

    @Autowired
    private ProductionProjectService productionProjectService; // 注入生产项目服务

    /**
     * 新增生产项目
     * @param projectAddDTO 项目信息
     * @return Result
     */
    @Operation(summary = "新增生产项目", description = "创建新的生产项目")
    @PostMapping
    public Result addProject(@RequestBody ProductionProjectAddDTO projectAddDTO) {
        log.info("新增生产项目: {}", projectAddDTO);
        return productionProjectService.addProject(projectAddDTO);
    }

    /**
     * 更新项目状态
     * @param statusUpdateDTO 状态更新信息
     * @return Result
     */
    @Operation(summary = "更新项目状态", description = "修改生产项目状态")
    @PutMapping("/status")
    public Result updateProjectStatus(@RequestBody ProductionStatusUpdateDTO statusUpdateDTO) {
        log.info("更新项目状态: {}", statusUpdateDTO);
        return productionProjectService.updateProjectStatus(statusUpdateDTO);
    }

    /**
     * 查询项目详情
     * @param id 项目ID
     * @return Result<ProductionProjectVO>
     */
    @Operation(summary = "查询项目详情", description = "根据ID查询生产项目详细信息")
    @GetMapping("/{id}")
    public Result<ProductionProjectVO> getProjectById(@PathVariable int id) {
        log.info("查询项目详情, ID: {}", id);
        return productionProjectService.getProjectById(id);
    }

    /**
     * 分页查询项目列表
     * @param currentPage 当前页码，默认为1
     * @param pageSize 每页显示数量，默认为10
     * @param queryDTO 查询条件
     * @return Result<page<ProductionProjectVO>>
     */
    @Operation(summary = "分页查询项目", description = "根据条件分页查询生产项目列表")
    @GetMapping("/list")
    public Result<page<ProductionProjectVO>> getProjectList(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            ProductionProjectQueryDTO queryDTO) {
        log.info("分页查询项目列表: 页码={}, 每页数量={}, 查询条件={}", currentPage, pageSize, queryDTO);
        return productionProjectService.getProjectList(currentPage, pageSize, queryDTO);
    }
}